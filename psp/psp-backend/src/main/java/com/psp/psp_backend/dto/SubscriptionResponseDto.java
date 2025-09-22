package com.psp.psp_backend.dto;

import com.psp.psp_backend.mapper.DtoEntity;

import java.util.Date;

public class SubscriptionResponseDto implements DtoEntity {
    private String merchantSubscriptionId;
    private String acquirerOrderId;
    private String paymentId;
    private Date acquirerTimestamp;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMerchantSubscriptionId() {
        return merchantSubscriptionId;
    }

    public void setMerchantSubscriptionId(String merchantSubscriptionId) {
        this.merchantSubscriptionId = merchantSubscriptionId;
    }

    public String getAcquirerOrderId() {
        return acquirerOrderId;
    }

    public void setAcquirerOrderId(String acquirerOrderId) {
        this.acquirerOrderId = acquirerOrderId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public Date getAcquirerTimestamp() {
        return acquirerTimestamp;
    }

    public void setAcquirerTimestamp(Date acquirerTimestamp) {
        this.acquirerTimestamp = acquirerTimestamp;
    }
}
