package com.webshop.webshop_backend.controller;

import com.webshop.webshop_backend.dto.BundleDto;
import com.webshop.webshop_backend.dto.BundleProductDto;
import com.webshop.webshop_backend.dto.ProductDto;
import com.webshop.webshop_backend.dto.PurchaseDto;
import com.webshop.webshop_backend.service.BundleProductService;
import com.webshop.webshop_backend.service.BundleService;
import com.webshop.webshop_backend.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/bundles")
public class BundleController {
    private BundleService bundleService;
    private BundleProductService bundleProductService;
    @Autowired
    private TransactionService transactionService;

    @Autowired
    public BundleController(BundleService bundleService, BundleProductService bundleProductService) {
        this.bundleService = bundleService;
        this.bundleProductService = bundleProductService;
    }
    @GetMapping("")
    public List<BundleDto> getBundles() {
        return bundleService.getBundles();
    }

    @GetMapping("/{id}")
    public  BundleDto getBundle(@PathVariable("id") String id) {
        return bundleService.getBundle(id);
    }

    @GetMapping("/bundle-products/{bundleid}")
    public List<BundleProductDto> getBundleProductsByBundle(@PathVariable("bundleid") String id) {
        return bundleProductService.getBundleProductsByBundle(id);
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