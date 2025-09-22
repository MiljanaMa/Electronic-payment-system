package com.psp.psp_backend.dto;

import com.psp.psp_backend.mapper.DtoEntity;

import java.time.LocalDate;
import java.util.Date;

public class MerchantSubscriptionDto implements DtoEntity {
    private String merchantId;
    private String merchantPass;
    private double amount;
    private String productId;
    private String productName;
    private String productDescription;
    private String merchantSubscriptionId;

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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getMerchantSubscriptionId() {
        return merchantSubscriptionId;
    }

    public void setMerchantSubscriptionId(String merchantSubscriptionId) {
        this.merchantSubscriptionId = merchantSubscriptionId;
    }

    public LocalDate getMerchantTimestamp() {
        return merchantTimestamp;
    }

    public void setMerchantTimestamp(LocalDate merchantTimestamp) {
        this.merchantTimestamp = merchantTimestamp;
    }

    private LocalDate merchantTimestamp;
}
