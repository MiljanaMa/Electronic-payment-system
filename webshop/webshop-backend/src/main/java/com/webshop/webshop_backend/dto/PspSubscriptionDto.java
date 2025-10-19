package com.webshop.webshop_backend.dto;

import com.webshop.webshop_backend.mapper.DtoEntity;

public class PspSubscriptionDto implements DtoEntity {
    private String merchantSubscriptionId;
    private String status;

    public String getMerchantSubscriptionId() {
        return merchantSubscriptionId;
    }

    public void setMerchantSubscriptionId(String merchantSubscriptionId) {
        this.merchantSubscriptionId = merchantSubscriptionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
