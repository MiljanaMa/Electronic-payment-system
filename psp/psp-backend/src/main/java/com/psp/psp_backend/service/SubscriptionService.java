package com.psp.psp_backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.psp.psp_backend.dto.*;
import com.psp.psp_backend.model.Client;
import com.psp.psp_backend.model.PaymentMethod;
import com.psp.psp_backend.model.Subscription;
import com.psp.psp_backend.model.Transaction;
import com.psp.psp_backend.repository.ClientRepository;
import com.psp.psp_backend.repository.PaymentMethodRepository;
import com.psp.psp_backend.repository.SubscriptionRepository;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class SubscriptionService {
    @Autowired
    SubscriptionRepository subscriptionRepository;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    PaymentMethodRepository paymentMethodRepository;
    private WebClient webClient;
    @Value("${transaction.api.url}")
    private String transactionApiUrl;
    @Value("${frontend.base.url}")
    private String frontendBaseUrl;
    @PostConstruct
    public void init() {
        if (transactionApiUrl != null && !transactionApiUrl.isEmpty()) {
            this.webClient = WebClient.builder()
                    .baseUrl(transactionApiUrl)
                    .build();
        } else {
            throw new IllegalArgumentException("Transaction API URL is not set in the properties file.");
        }
    }
    public String save(MerchantSubscriptionDto merchantSubscriptionDto) throws Exception {
        Client client = clientRepository.findByMerchantIdAndPass(merchantSubscriptionDto.getMerchantId(), merchantSubscriptionDto.getMerchantPass()).get();
        if(client == null)
            throw new Exception("Client is not found");
        Subscription subscription = new Subscription(merchantSubscriptionDto, client);
        //za sada ne pravi transakciju za svaku pretplatu
        //Transaction transaction = new Transaction(merchantTransactionDto, client);
        return subscriptionRepository.save(subscription).getId();
    }
    public String sendSubscription(SubscriptionCheckoutDto subscriptionCheckoutDto) throws Exception {
        Client client = clientRepository.findByMerchantId(subscriptionCheckoutDto.getMerchantId()).get();
        if (client == null)
            throw new Exception("Client is not found");
        Subscription subscription = subscriptionRepository.findById(subscriptionCheckoutDto.getSubscriptionId()).get();
        if (subscription == null)
            throw new Exception("Subscription is not found");
        PaymentMethod paymentMethod = paymentMethodRepository.getReferenceById(UUID.fromString(subscriptionCheckoutDto.getPaymentMethodId()));
        switch (paymentMethod.getName()) {
            case "bank":
                //Anja treba dodati logiku za banku i pretplate
                return "";
            case "paypal":
                Map<String, Object> payPalPayload = makePayPalRequest(subscription);
                return initiatePayPalPayment(payPalPayload);
            default:
                throw new Exception("Unsupported payment method: " + paymentMethod);
        }
    }
    public WebClient createSecureWebClient(String url) throws Exception {
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

        WebClient webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(url)
                .build();
        return webClient;
    }
    private String initiatePayPalPayment(Map<String, Object> payPalPayload) {
        try {
            WebClient webClient = createSecureWebClient("https://localhost:8087");
            String response =  webClient.post()
                    .uri("subscriptions/initiate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payPalPayload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> responseMap = objectMapper.readValue(response, new TypeReference<Map<String, String>>() {});

            // Vrati ceo map ili samo approve_url, zavisi šta frontend očekuje
            // Ako frontend očekuje approve_url:
            return responseMap.get("approve_url");
        } catch (Exception e) {
            throw new RuntimeException("Failed to communicate with Bank backend", e);
        }
    }

    private Map<String, Object> makePayPalRequest(Subscription subscription) {
        Map<String, Object> payPalPayload = new HashMap<>();
        payPalPayload.put("merchant_id", subscription.getClient().getMerchantId());
        payPalPayload.put("merchant_password", subscription.getClient().getMerchantPassword());
        payPalPayload.put("amount", subscription.getAmount());
        payPalPayload.put("product_id", subscription.getProductId());
        payPalPayload.put("product_name", subscription.getProductName());
        payPalPayload.put("product_description", subscription.getProductDescription());
        payPalPayload.put("merchant_subscription_id", subscription.getMerchantSubscriptionId());
        payPalPayload.put("success_url", frontendBaseUrl + "/success/sub");
        payPalPayload.put("failed_url", frontendBaseUrl + "/failed/sub");
        payPalPayload.put("error_url", frontendBaseUrl + "/error/sub");

        return payPalPayload;
    }
    public void updateSubscription(SubscriptionResponseDto subscriptionResponseDto) throws Exception {
        Subscription subscription = subscriptionRepository.findByMerchantSubscriptionId(subscriptionResponseDto.getMerchantSubscriptionId()).get();
        if(subscription == null)
            throw new Exception("Transaction is not found");
        Map<String, Object> payload = makeClientRequest(subscription, subscriptionResponseDto.getStatus());
        sendClientSubscriptionUpdate(payload);
    }
    private static Map<String, Object> makeClientRequest(Subscription subscription, String status) {
        Map<String, Object> webShopPayload = new HashMap<>();
        webShopPayload.put("merchantSubscriptionId", subscription.getMerchantSubscriptionId());
        webShopPayload.put("status", status);
        return webShopPayload;
    }
    private void sendClientSubscriptionUpdate(Map<String, Object> payload) {
        try {
            WebClient webClient = createSecureWebClient("https://localhost:8081");
            String response =  webClient.post()
                    .uri("/api/bundles/subscription/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ObjectMapper objectMapper = new ObjectMapper();
        } catch (Exception e) {
            throw new RuntimeException("Failed to communicate with Webshop backend", e);
        }
    }
}
