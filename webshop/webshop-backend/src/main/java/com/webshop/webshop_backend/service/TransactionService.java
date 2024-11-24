package com.webshop.webshop_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webshop.webshop_backend.dto.BundleProductDto;
import com.webshop.webshop_backend.dto.ProductDto;
import com.webshop.webshop_backend.dto.PurchaseDto;
import com.webshop.webshop_backend.dto.TransactionDto;
import com.webshop.webshop_backend.model.*;
import com.webshop.webshop_backend.model.enums.TransactionType;
import com.webshop.webshop_backend.repository.BundleProductRepository;
import com.webshop.webshop_backend.repository.ProductRepository;
import com.webshop.webshop_backend.repository.TransactionRepository;
import com.webshop.webshop_backend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.http.HttpHeaders;
import java.util.HashMap;
import java.util.Map;

@Service
public class TransactionService {
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    BundleProductRepository bundleProductRepository;
    @Autowired
    MerchantService merchantService;
    @Value("${transaction.api.url}")
    private String transactionApiUrl;
    private WebClient webClient;
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

    public Map<String, Object> createTransaction(PurchaseDto purchaseDto, String username) throws Exception {
        User user = userRepository.findByUsername(username).get();
        if(user == null)
            throw new Exception("User doesn't exist");

        Product product;
        BundleProduct bundleProduct;
        Transaction transaction;
        if(purchaseDto.getPurchaseType().equalsIgnoreCase("PRODUCT")){
            product = productRepository.findById(purchaseDto.getPurchaseId()).get();
            if(product == null)
                throw new Exception("Product doesn't exist.");
            transaction = new Transaction(product.getPrice(), TransactionType.PRODUCT, user, product.getId());
        }else{
            bundleProduct = bundleProductRepository.findById(purchaseDto.getPurchaseId()).get();
            if(bundleProduct == null)
                throw new Exception("Product doesn't exist.");
            //check amount
            double amount = bundleProduct.getProduct().getPrice() + bundleProduct.getBundle().getPrice();
            transaction = new Transaction(amount , TransactionType.BUNDLE, user, bundleProduct.getId());
        }
        Transaction savedTransaction = transactionRepository.save(transaction);
        String redirectUrl = sendTransaction(savedTransaction);

        Map<String, Object> response = new HashMap<>();
        response.put("redirectUrl", redirectUrl);
        return response;
    }

    public String sendTransaction(Transaction transaction) {
        MerchantCredentials credentials = merchantService.getMerchantCredentials();

        Map<String, Object> pspPayload = makePspRequest(transaction, credentials);

        String pspResponse = initiatePayment(pspPayload);

        return extractRedirectUrl(pspResponse);
    }

    private static Map<String, Object> makePspRequest(Transaction transaction, MerchantCredentials credentials) {
        Map<String, Object> pspPayload = new HashMap<>();
        pspPayload.put("merchantId", credentials.getMerchantId());
        pspPayload.put("merchantPass", credentials.getMerchantPass());
        pspPayload.put("amount", transaction.getAmount());
        pspPayload.put("merchantTransactionId", transaction.getId());
        pspPayload.put("merchantTimestamp", transaction.getTimestamp());
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

}
