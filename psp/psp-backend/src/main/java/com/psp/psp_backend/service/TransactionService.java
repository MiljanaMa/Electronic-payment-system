package com.psp.psp_backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.psp.psp_backend.dto.BankResponseDto;
import com.psp.psp_backend.dto.MerchantTransactionDto;
import com.psp.psp_backend.dto.PaymentCheckoutDto;
import com.psp.psp_backend.model.Client;
import com.psp.psp_backend.model.Transaction;
import com.psp.psp_backend.repository.ClientRepository;
import com.psp.psp_backend.repository.TransactionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Service
public class TransactionService {
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    ClientRepository clientRepository;
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
    public String save(MerchantTransactionDto merchantTransactionDto) throws Exception {
        Client client = clientRepository.findByMerchantIdAndPass(merchantTransactionDto.getMerchantId(), merchantTransactionDto.getMerchantPass()).get();
        if(client == null)
            throw new Exception("Client is not found");
        Transaction transaction = new Transaction(merchantTransactionDto, client);
        return transactionRepository.save(transaction).getId();
    }
    public void updateTransaction(BankResponseDto bankResponseDto) throws Exception {
        Transaction transaction = transactionRepository.findByMerchantOrderId(bankResponseDto.getMerchantOrderId()).get();
        if(transaction == null)
            throw new Exception("Transaction is not found");
        Map<String, Object> payload = makeClientRequest(transaction, bankResponseDto.getStatus());
        sendClientTransactionUpdate(payload);
    }
    public String sendPayment(PaymentCheckoutDto paymentCheckoutDto) throws Exception {
        Client client = clientRepository.findByMerchantId(paymentCheckoutDto.getMerchantId()).get();
        if(client == null)
            throw new Exception("Client is not found");
        Transaction transaction = transactionRepository.findById(paymentCheckoutDto.getTransactionId()).get();
        if(transaction == null)
            throw new Exception("Transaction is not found");
        Map<String, Object> payload = makeBankRequest(transaction);
        Map<String, String> bankResponse = initiatePayment(payload);
        return bankResponse.get("PAYMENT_URL");
    }
    private static Map<String, Object> makeBankRequest(Transaction transaction) {
        Map<String, Object> bankPayload = new HashMap<>();
        bankPayload.put("MERCHANT_ID", transaction.getClient().getMerchantId());
        bankPayload.put("MERCHANT_PASSWORD", transaction.getClient().getMerchantPassword());
        bankPayload.put("AMOUNT", transaction.getAmount());
        bankPayload.put("MERCHANT_ORDER_ID", transaction.getMerchantTransactionId());
        bankPayload.put("MERCHANT_TIMESTAMP", transaction.getMerchantTimestamp());
        bankPayload.put("SUCCESS_URL", "http://localhost:3001/success");
        bankPayload.put("FAILED_URL", "http://localhost:3001/failed");
        bankPayload.put("ERROR_URL", "http://localhost:3001/error");
        return bankPayload;
    }
    private static Map<String, Object> makeClientRequest(Transaction transaction, String status) {
        Map<String, Object> webShopPayload = new HashMap<>();
        webShopPayload.put("merchantOrderId", transaction.getMerchantTransactionId());
        webShopPayload.put("status", status);
        return webShopPayload;
    }

    private Map<String, String> initiatePayment(Map<String, Object> payload) {
        try {
            String response =  webClient.post()
                    .uri("")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> responseMap = objectMapper.readValue(response, new TypeReference<Map<String, String>>() {});

            return responseMap;
        } catch (Exception e) {
            throw new RuntimeException("Failed to communicate with Bank backend", e);
        }
    }
    private void sendClientTransactionUpdate(Map<String, Object> payload) {
        try {
            webClient.post()
                    .uri("http://localhost:8089/api/webshop/transactions/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            //delete later
            ObjectMapper objectMapper = new ObjectMapper();
        } catch (Exception e) {
            throw new RuntimeException("Failed to communicate with Bank backend", e);
        }
    }
    private String extractRedirectUrl(String bankResponse) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(bankResponse);
            return jsonNode.get("redirectUrl").asText(); // Extract redirect URL from response
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse PSP response", e);
        }
    }
}
