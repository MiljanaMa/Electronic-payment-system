package com.webshop.webshop_backend.controller;

import com.webshop.webshop_backend.dto.PspTransactionDto;
import com.webshop.webshop_backend.dto.TransactionDto;
import com.webshop.webshop_backend.service.AuthService;
import com.webshop.webshop_backend.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(value = "/api/webshop/transactions")
public class TransactionController {
    @Autowired
    TransactionService transactionService;
    @Autowired
    AuthService authService;

    @PostMapping("/update")
    public ResponseEntity<String> updateTransaction(@RequestBody PspTransactionDto transactionDto) {
        try {
            transactionService.updateTransaction(transactionDto);
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR");
        }
    }

    @GetMapping("")
    public List<TransactionDto> getTransactions() {
        String userId = authService.getLoggedUser().getId();
        return transactionService.getTransactionsByUser(userId);
    }

}
