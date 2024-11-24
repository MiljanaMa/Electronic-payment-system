package com.psp.psp_backend.model;

import io.micrometer.common.lang.NonNull;
import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true)
    private String id;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    private Client client;
    @Column(name = "amount", unique = false)
    @NonNull
    private double amount;
    @Column(name = "merchant_transaction_id")
    @NonNull
    private String merchantTransactionId;
    @Column(name = "merchant_timestamp")
    @NonNull
    private Date merchantTimestamp;

    public Transaction() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @NonNull
    public String getMerchantTransactionId() {
        return merchantTransactionId;
    }

    public void setMerchantTransactionId(@NonNull String merchantTransactionId) {
        this.merchantTransactionId = merchantTransactionId;
    }

    @NonNull
    public Date getMerchantTimestamp() {
        return merchantTimestamp;
    }

    public void setMerchantTimestamp(@NonNull Date merchantTimestamp) {
        this.merchantTimestamp = merchantTimestamp;
    }
}
