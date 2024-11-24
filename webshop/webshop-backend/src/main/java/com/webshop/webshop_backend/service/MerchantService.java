package com.webshop.webshop_backend.service;

import com.webshop.webshop_backend.model.MerchantCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MerchantService {

    @Value("${merchant.id}")
    private String merchantId;

    @Value("${merchant.pass}")
    private String merchantPass;

    public MerchantCredentials getMerchantCredentials() {
        MerchantCredentials credentials = new MerchantCredentials();
        credentials.setMerchantId(merchantId);
        credentials.setMerchantPass(merchantPass);
        return credentials;
    }
}
