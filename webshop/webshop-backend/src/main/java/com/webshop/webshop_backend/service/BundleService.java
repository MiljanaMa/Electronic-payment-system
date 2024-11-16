package com.webshop.webshop_backend.service;

import com.webshop.webshop_backend.dto.BundleDto;
import com.webshop.webshop_backend.dto.ProductDto;
import com.webshop.webshop_backend.mapper.DtoUtils;
import com.webshop.webshop_backend.model.Bundle;
import com.webshop.webshop_backend.repository.BundleRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class BundleService {
    private final BundleRepository bundleRepository;
    public BundleService(BundleRepository bundleRepository) {
        this.bundleRepository = bundleRepository;
    }
    public List<BundleDto> getBundles(){
        Set<BundleDto> bundleDtosSet = (Set<BundleDto>) new DtoUtils().convertToDtos(bundleRepository.findAllBundles(), new BundleDto());
        return new ArrayList<>(bundleDtosSet);
    }

    public BundleDto getBundle(String id){
        BundleDto bundleDto = (BundleDto) new DtoUtils().convertToDto(bundleRepository.findById(id), new BundleDto());
        return bundleDto;
    }
}