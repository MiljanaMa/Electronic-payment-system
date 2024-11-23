package com.webshop.webshop_backend.controller;

import com.webshop.webshop_backend.dto.ProductDto;
import com.webshop.webshop_backend.model.Product;
import com.webshop.webshop_backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(value = "/api/products")
public class ProductController {
    private ProductService productService;
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
}