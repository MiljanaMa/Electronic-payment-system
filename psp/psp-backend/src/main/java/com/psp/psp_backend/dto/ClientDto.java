package com.psp.psp_backend.dto;

import com.psp.psp_backend.mapper.DtoEntity;

import java.util.Set;

public class ClientDto extends UserDto {
    /*private String merchantId;
    private String merchantPassword;*/
    private Set<PaymentMethodInfoDto> paymentMethods;

    /*public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantPassword() {
        return merchantPassword;
    }

    public void setMerchantPassword(String merchantPassword) {
        this.merchantPassword = merchantPassword;
    }*/

    public Set<PaymentMethodInfoDto> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(Set<PaymentMethodInfoDto> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }
}
