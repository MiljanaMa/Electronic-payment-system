package com.webshop.webshop_backend.controller;

import com.webshop.webshop_backend.dto.PspTransactionDto;
import com.webshop.webshop_backend.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping(value = "/api/webshop/transactions")
public class TransactionController {
    @Autowired
    TransactionService transactionService;
    @PostMapping("/update")
    public ResponseEntity<String> updateTransaction(@RequestBody PspTransactionDto transactionDto) {
        try {
            transactionService.updateTransaction(transactionDto);
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR");
        }
    }
}
