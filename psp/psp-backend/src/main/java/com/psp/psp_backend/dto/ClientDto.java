package com.psp.psp_backend.dto;


import java.util.Set;

public class ClientDto extends UserDto {
    private Set<PaymentMethodInfoDto> paymentMethods;

    public Set<PaymentMethodInfoDto> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(Set<PaymentMethodInfoDto> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }
}
