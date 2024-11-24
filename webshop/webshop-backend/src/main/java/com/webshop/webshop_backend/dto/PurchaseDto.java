package com.webshop.webshop_backend.dto;

import jakarta.validation.constraints.NotEmpty;

public class PurchaseDto {
    @NotEmpty(message = "Purchase id is required")
    private String purchaseId;
    @NotEmpty(message = "Purchase type id  required")
    private String purchaseType;

    public String getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(String purchaseId) {
        this.purchaseId = purchaseId;
    }

    public String getPurchaseType() {
        return purchaseType;
    }

    public void setPurchaseType(String purchaseType) {
        this.purchaseType = purchaseType;
    }
}
