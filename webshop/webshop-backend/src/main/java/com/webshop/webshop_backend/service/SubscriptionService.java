package com.webshop.webshop_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webshop.webshop_backend.dto.PspSubscriptionDto;
import com.webshop.webshop_backend.dto.PspTransactionDto;
import com.webshop.webshop_backend.dto.PurchaseDto;
import com.webshop.webshop_backend.model.*;
import com.webshop.webshop_backend.model.enums.SubscriptionStatus;
import com.webshop.webshop_backend.model.enums.TransactionStatus;
import com.webshop.webshop_backend.repository.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class SubscriptionService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    SubscriptionRepository subscriptionRepository;
    @Autowired
    BundleProductRepository bundleProductRepository;
    @Autowired
    MerchantService merchantService;
    @Autowired
    TransactionService transactionService;
    @Value("${subscription.api.url}")
    private String subscriptionApiUrl;
    private WebClient webClient;
    @PostConstruct
    public void init() throws Exception{
        if (subscriptionApiUrl != null && !subscriptionApiUrl.isEmpty()) {
            KeyStore trustStore = KeyStore.getInstance("PKCS12");
            try (InputStream trustStoreStream = new ClassPathResource("truststore.jks").getInputStream()) {
                trustStore.load(trustStoreStream, "truststorepassword".toCharArray());
            }

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            SslContext nettySslContext = SslContextBuilder.forClient()
                    .trustManager(tmf)
                    .build();

            HttpClient httpClient = HttpClient.create()
                    .secure(spec -> spec.sslContext(nettySslContext));

            this.webClient = WebClient.builder()
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .baseUrl(subscriptionApiUrl)
                    .build();
        } else {
            throw new IllegalArgumentException("Transaction API URL is not set in the properties file.");
        }
    }
    public Map<String, Object> createSubscription(PurchaseDto purchaseDto, String username) throws Exception {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("User doesn't exist"));

        Optional<Subscription> subscription = subscriptionRepository.findByUserAndPurchaseIdAndStatus(user, purchaseDto.getPurchaseId(), SubscriptionStatus.ACTIVE);
        if (subscription.isPresent())
            throw new Exception("Subscription already exists");

        BundleProduct bundleProduct = bundleProductRepository.findById(purchaseDto.getPurchaseId()).get();
        if(bundleProduct == null)
            throw new Exception("Bundle doesn't exist.");
        Subscription newSubscription = makeSubscription(purchaseDto, user, bundleProduct);

        Subscription savedSubscription = subscriptionRepository.save(newSubscription);

        Transaction savedTransaction = transactionService.createSubscriptionTransaction(savedSubscription);

        // Dobavljanje redirect URL-a ka payment gateway-u za recurring
        String redirectUrl = sendSubscriptionRequest(savedSubscription);

        MerchantCredentials credentials = merchantService.getMerchantCredentials();

        Map<String, Object> response = new HashMap<>();
        response.put("redirectUrl", redirectUrl);
        response.put("merchantId", credentials.getMerchantId());
        response.put("subscriptionId", savedSubscription.getId());
        return response;
    }

    private static Subscription makeSubscription(PurchaseDto purchaseDto, User user, BundleProduct bundleProduct) {
        //Zasto ima i bundle i product
        double amount = bundleProduct.getProduct().getPrice() + bundleProduct.getBundle().getPrice();

        Subscription newSubscription = new Subscription();
        newSubscription.setUser(user);
        newSubscription.setAmount(amount);
        newSubscription.setStartDate(LocalDate.now());
        newSubscription.setEndDate(LocalDate.now().plusYears(1));
        newSubscription.setStatus(SubscriptionStatus.PENDING);
        newSubscription.setPurchaseId(purchaseDto.getPurchaseId());
        newSubscription.setBundleProduct(bundleProduct);
        return newSubscription;
    }
    public String sendSubscriptionRequest(Subscription subscription) {
        MerchantCredentials credentials = merchantService.getMerchantCredentials();

        Map<String, Object> pspPayload = makePspRequest(subscription, credentials);

        String pspResponse = initiatePayment(pspPayload);

        return extractRedirectUrl(pspResponse);
    }
    private static Map<String, Object> makePspRequest(Subscription subscription, MerchantCredentials credentials) {
        Map<String, Object> pspPayload = new HashMap<>();
        pspPayload.put("merchantId", credentials.getMerchantId());
        pspPayload.put("merchantPass", credentials.getMerchantPass());
        pspPayload.put("amount", subscription.getAmount());
        pspPayload.put("productId", subscription.getBundleProduct().getBundle().getId());
        pspPayload.put("productName", subscription.getBundleProduct().getBundle().getName());
        pspPayload.put("productDescription", subscription.getBundleProduct().getBundle().getDescription());
        pspPayload.put("merchantSubscriptionId", subscription.getId());
        pspPayload.put("merchantTimestamp", subscription.getStartDate());
        return pspPayload;
    }

    private String initiatePayment(Map<String, Object> payload) {
        try {
            return webClient.post()
                    .uri("")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Failed to communicate with PSP backend", e);
        }
    }

    private String extractRedirectUrl(String pspResponse) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(pspResponse);
            return jsonNode.get("redirectUrl").asText(); // Extract redirect URL from response
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse PSP response", e);
        }
    }
    public void updateSubscription(PspSubscriptionDto pspSubscriptionDto) throws Exception {
        Subscription subscription = subscriptionRepository.findById(pspSubscriptionDto.getMerchantSubscriptionId()).get();
        if (subscription == null)
            throw new Exception("Subscription doesn't exist");
        if(pspSubscriptionDto.getStatus().equals("ACTIVE"))
            subscription.setStatus(SubscriptionStatus.ACTIVE);
        else
            subscription.setStatus(SubscriptionStatus.FAILED);
        subscriptionRepository.save(subscription);
    }
}
