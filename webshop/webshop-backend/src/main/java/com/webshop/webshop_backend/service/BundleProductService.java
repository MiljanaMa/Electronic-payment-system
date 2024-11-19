package com.webshop.webshop_backend.service;

import com.webshop.webshop_backend.dto.BundleDto;
import com.webshop.webshop_backend.dto.BundleProductDto;
import com.webshop.webshop_backend.mapper.DtoUtils;
import com.webshop.webshop_backend.model.BundleProduct;
import com.webshop.webshop_backend.repository.BundleProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class BundleProductService {
    private final BundleProductRepository bundleProductRepository;
    public BundleProductService(BundleProductRepository bundleProductRepository) {
        this.bundleProductRepository = bundleProductRepository;
    }
    public List<BundleProductDto> getBundleProductsByBundle(String id){
        Set<BundleProductDto> bundleProductDtosSet = (Set<BundleProductDto>) new DtoUtils().convertToDtos(bundleProductRepository.findBundleProductsByBundle(id), new BundleProductDto());
        return new ArrayList<>(bundleProductDtosSet);
    }
    public BundleProductDto getBundleProduct(String id){
        BundleProductDto bundleProductDto = (BundleProductDto) new DtoUtils().convertToDto(bundleProductRepository.findById(id), new BundleDto());
        return bundleProductDto;
    }
}