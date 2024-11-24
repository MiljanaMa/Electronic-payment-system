package com.webshop.webshop_backend.model;

public class MerchantCredentials {
    private String merchantId;
    private String merchantPass;

    public MerchantCredentials() {
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantPass() {
        return merchantPass;
    }

    public void setMerchantPass(String merchantPass) {
        this.merchantPass = merchantPass;
    }
}
