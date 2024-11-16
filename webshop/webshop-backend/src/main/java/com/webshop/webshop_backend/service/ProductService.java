package com.webshop.webshop_backend.service;

import com.webshop.webshop_backend.model.Product;
import com.webshop.webshop_backend.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository serviceRepository;
    public ProductService(ProductRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }
    public List<Product> getProducts(){
        return serviceRepository.findAll();
    }
    public Product getProduct(String id){
        return serviceRepository.findById(id).orElse(null);
    }
}