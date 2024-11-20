package com.psp.psp_backend.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "clients")
public class Client extends User {
    @Column(name="merchant_id", nullable = false, unique = true)
    private String merchantId;
    @Column(name="merchant_password", nullable = false, unique = true)
    private String merchantPassword;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "client_payment_method",
            joinColumns = @JoinColumn(name = "client_user_id"),
            inverseJoinColumns = @JoinColumn(name = "payment_method_id")
    )
    private Set<PaymentMethod> paymentMethods = new HashSet<>();

    public Client() {
    }

    public String getMerchantId() {
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
    }

    public Set<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(Set<PaymentMethod> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }
}
