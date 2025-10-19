package com.psp.psp_backend.controller;

import com.psp.psp_backend.dto.BankResponseDto;
import com.psp.psp_backend.dto.MerchantSubscriptionDto;
import com.psp.psp_backend.dto.SubscriptionCheckoutDto;
import com.psp.psp_backend.dto.SubscriptionResponseDto;
import com.psp.psp_backend.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/subscription")
public class SubscriptionController {
    @Value("${frontend.url}")
    private String frontendUrl;
    @Autowired
    SubscriptionService subscriptionService;

    @PostMapping("")
    public ResponseEntity<Map<String, Object>> saveMerchantSubscription(@RequestBody MerchantSubscriptionDto merchantSubscriptionDto){
        try {
            String subscriptionId = subscriptionService.save(merchantSubscriptionDto);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Subscription saved successfully");
            response.put("redirectUrl", frontendUrl + "?subscriptionId=" + subscriptionId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to save merchant subscription: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PostMapping("/checkout")
    public ResponseEntity<String> checkoutSubscription(@RequestBody SubscriptionCheckoutDto subscriptionCheckoutDto){
        System.out.println("CheckoutPayment called: ");
        try {
            String response = subscriptionService.sendSubscription(subscriptionCheckoutDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @PostMapping("update")
    public ResponseEntity<String> updateSubscription(@RequestBody SubscriptionResponseDto subscriptionResponseDto){
        try {
            subscriptionService.updateSubscription(subscriptionResponseDto);
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
