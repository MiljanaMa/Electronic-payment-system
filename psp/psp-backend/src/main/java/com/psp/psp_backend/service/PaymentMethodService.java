package com.psp.psp_backend.service;

import com.psp.psp_backend.dto.ClientDto;
import com.psp.psp_backend.dto.PaymentMethodInfoDto;
import com.psp.psp_backend.mapper.DtoUtils;
import com.psp.psp_backend.model.Client;
import com.psp.psp_backend.model.PaymentMethod;
import com.psp.psp_backend.model.Transaction;
import com.psp.psp_backend.repository.ClientRepository;
import com.psp.psp_backend.repository.PaymentMethodRepository;
import com.psp.psp_backend.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class PaymentMethodService {
    @Autowired
    PaymentMethodRepository paymentMethodRepository;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    ClientRepository clientRepository;
    public Set<PaymentMethodInfoDto> getAll(){
        Set<PaymentMethod> paymentMethods = new HashSet<>(paymentMethodRepository.findAll());
        return (Set<PaymentMethodInfoDto>) new DtoUtils().convertToDtos(paymentMethods, new PaymentMethodInfoDto());
    }
    public Set<PaymentMethodInfoDto> getByTransactionId(String transactionId, String merchantId) throws Exception {
        Client client = clientRepository.findByMerchantId(merchantId).get();
        if(client == null)
            throw new Exception("Merchant is not valid");
        Transaction transaction = transactionRepository.findById(transactionId).get();
        if(transaction == null)
            throw new Exception("Transaction is not found");
        if(transaction.getClient() != client)
            throw new Exception("Transaction from not valid merchant");

        Set<PaymentMethod> paymentMethods = new HashSet<>(transaction.getClient().getPaymentMethods());
        return (Set<PaymentMethodInfoDto>) new DtoUtils().convertToDtos(paymentMethods, new PaymentMethodInfoDto());
    }
}
