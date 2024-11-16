package com.webshop.webshop_backend.controller;

import com.webshop.webshop_backend.model.Product;
import com.webshop.webshop_backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @GetMapping("")
    public List<Product> getProducts() {
        return productService.getProducts();
    }
    @GetMapping("/{id}")
    public Product getProduct(@PathVariable("id") String id) {
        return productService.getProduct(id);
    }
}