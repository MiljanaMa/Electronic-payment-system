package com.webshop.webshop_backend.controller;

import com.webshop.webshop_backend.dto.BundleDto;
import com.webshop.webshop_backend.dto.BundleProductDto;
import com.webshop.webshop_backend.model.Bundle;
import com.webshop.webshop_backend.service.BundleProductService;
import com.webshop.webshop_backend.service.BundleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/bundles")
public class BundleController {
    private BundleService bundleService;
    private BundleProductService bundleProductService;

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
}