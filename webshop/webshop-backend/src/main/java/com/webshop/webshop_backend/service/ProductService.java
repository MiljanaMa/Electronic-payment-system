package com.webshop.webshop_backend.service;

import com.webshop.webshop_backend.dto.ProductDto;
import com.webshop.webshop_backend.mapper.DtoUtils;
import com.webshop.webshop_backend.model.Product;
import com.webshop.webshop_backend.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductDto> getProducts(){
        Set<ProductDto> productDtosSet = (Set<ProductDto>) new DtoUtils().convertToDtos(productRepository.findAllProducts(), new ProductDto());
        return new ArrayList<>(productDtosSet);
    }
    public ProductDto getProduct(String id){
        ProductDto productDto = (ProductDto) new DtoUtils().convertToDto(productRepository.findById(id), new ProductDto());
        return productDto;
    }
}