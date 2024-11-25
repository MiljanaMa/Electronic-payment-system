package com.webshop.webshop_backend.dto;

import com.webshop.webshop_backend.mapper.DtoEntity;

public class PspTransactionDto implements DtoEntity {
    private String merchantOrderId;
    private String status;

    public String getMerchantOrderId() {
        return merchantOrderId;
    }

    public void setMerchantOrderId(String merchantOrderId) {
        this.merchantOrderId = merchantOrderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
