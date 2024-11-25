package com.psp.psp_backend.controller;

import com.psp.psp_backend.dto.BankResponseDto;
import com.psp.psp_backend.dto.MerchantTransactionDto;
import com.psp.psp_backend.dto.PaymentCheckoutDto;
import com.psp.psp_backend.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/api/transaction")
public class TransactionController {
    @Value("${frontend.url}")
    private String frontendUrl;
    @Autowired
    TransactionService transactionService;

    @PostMapping("")
    public ResponseEntity<Map<String, Object>> saveMerchantTransaction(@RequestBody MerchantTransactionDto merchantTransactionDto){
        try {
            String transactionId = transactionService.save(merchantTransactionDto);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Transaction saved successfully");
            response.put("redirectUrl", frontendUrl + "?transactionId=" + transactionId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to save merchant transaction: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
    @PostMapping("checkout")
    public ResponseEntity<String> checkoutPayment(@RequestBody PaymentCheckoutDto paymentCheckoutDto){
        try {
            String response = transactionService.sendPayment(paymentCheckoutDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @PostMapping("update")
    public ResponseEntity<String> updateTransaction(@RequestBody BankResponseDto bankResponseDto){
        try {
            transactionService.updateTransaction(bankResponseDto);
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
