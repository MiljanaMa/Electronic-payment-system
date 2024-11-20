package com.psp.psp_backend.service;

import com.psp.psp_backend.dto.ClientDto;
import com.psp.psp_backend.dto.PaymentMethodInfoDto;
import com.psp.psp_backend.mapper.DtoUtils;
import com.psp.psp_backend.model.Client;
import com.psp.psp_backend.model.PaymentMethod;
import com.psp.psp_backend.repository.PaymentMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class PaymentMethodService {
    @Autowired
    PaymentMethodRepository paymentMethodRepository;
    public Set<PaymentMethodInfoDto> getAll(){
        Set<PaymentMethod> paymentMethods = new HashSet<>(paymentMethodRepository.findAll());
        return (Set<PaymentMethodInfoDto>) new DtoUtils().convertToDtos(paymentMethods, new PaymentMethodInfoDto());
    }
}
