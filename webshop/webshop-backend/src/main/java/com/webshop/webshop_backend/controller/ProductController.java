package com.webshop.webshop_backend.controller;

import com.webshop.webshop_backend.dto.ProductDto;
import com.webshop.webshop_backend.dto.PurchaseDto;
import com.webshop.webshop_backend.dto.TransactionDto;
import com.webshop.webshop_backend.model.Product;
import com.webshop.webshop_backend.service.ProductService;
import com.webshop.webshop_backend.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private TransactionService transactionService;
    private WebClient webClient;
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("")
    public List<ProductDto> getProducts() {
        return productService.getProducts();
    }

    @GetMapping("/{id}")
    public ProductDto getProduct(@PathVariable("id") String id) {
        return productService.getProduct(id);
    }
    @PostMapping("/buy")
    public ResponseEntity<Map<String, Object>> buyProduct(@RequestBody PurchaseDto purchaseDto, Principal user) {
        try {
            Map<String, Object> transaction = transactionService.createTransaction(purchaseDto, user.getName());
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process the transaction: " + e.getMessage()));
        }
    }
}