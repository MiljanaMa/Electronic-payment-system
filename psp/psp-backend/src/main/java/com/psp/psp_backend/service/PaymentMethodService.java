package com.psp.psp_backend.service;

import com.psp.psp_backend.dto.ClientDto;
import com.psp.psp_backend.dto.PaymentMethodInfoDto;
import com.psp.psp_backend.mapper.DtoUtils;
import com.psp.psp_backend.model.Client;
import com.psp.psp_backend.model.PaymentMethod;
import com.psp.psp_backend.model.Subscription;
import com.psp.psp_backend.model.Transaction;
import com.psp.psp_backend.repository.ClientRepository;
import com.psp.psp_backend.repository.PaymentMethodRepository;
import com.psp.psp_backend.repository.SubscriptionRepository;
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
    @Autowired
    SubscriptionRepository subscriptionRepository;
    public Set<PaymentMethodInfoDto> getAll(){
        Set<PaymentMethod> paymentMethods = new HashSet<>(paymentMethodRepository.findAll());
        return (Set<PaymentMethodInfoDto>) new DtoUtils().convertToDtos(paymentMethods, new PaymentMethodInfoDto());
    }
    public Set<PaymentMethodInfoDto> getById(String id, String merchantId, String paymentType) throws Exception {
        Client client = clientRepository.findByMerchantId(merchantId).get();
        if(client == null)
            throw new Exception("Merchant is not valid");
        Set<PaymentMethod> paymentMethods = new HashSet<>();;
        if(paymentType.equals("transaction")) {
            Transaction transaction = transactionRepository.findById(id).get();
            if(transaction == null)
                throw new Exception("Transaction is not found");
            if(transaction.getClient() != client)
                throw new Exception("Transaction from not valid merchant");
            paymentMethods = new HashSet<>(transaction.getClient().getPaymentMethods());
        }
        else if(paymentType.equals("subscription")){
            Subscription subscription = subscriptionRepository.findById(id).get();
            if(subscription == null)
                throw new Exception("Transaction is not found");
            if(subscription.getClient() != client)
                throw new Exception("Transaction from not valid merchant");
            paymentMethods = new HashSet<>(subscription.getClient().getPaymentMethods());
        }
        return (Set<PaymentMethodInfoDto>) new DtoUtils().convertToDtos(paymentMethods, new PaymentMethodInfoDto());
    }
    public Set<PaymentMethodInfoDto> getBySubscriptionId(String subscriptionId, String merchantId) throws Exception {
        Client client = clientRepository.findByMerchantId(merchantId).get();
        if(client == null)
            throw new Exception("Merchant is not valid");
        Subscription subscription = subscriptionRepository.findById(subscriptionId).get();
        if(subscription == null)
            throw new Exception("Transaction is not found");
        if(subscription.getClient() != client)
            throw new Exception("Transaction from not valid merchant");

        Set<PaymentMethod> paymentMethods = new HashSet<>(subscription.getClient().getPaymentMethods());
        return (Set<PaymentMethodInfoDto>) new DtoUtils().convertToDtos(paymentMethods, new PaymentMethodInfoDto());
    }
}
