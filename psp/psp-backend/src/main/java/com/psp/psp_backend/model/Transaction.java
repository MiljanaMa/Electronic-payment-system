package com.psp.psp_backend.model;

import com.psp.psp_backend.dto.MerchantTransactionDto;
import io.micrometer.common.lang.NonNull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    public Transaction(MerchantTransactionDto merchantTransactionDto, Client client) {
        this.client = client;
        this.amount = merchantTransactionDto.getAmount();
        this.merchantTransactionId = merchantTransactionDto.getMerchantTransactionId();
        this.merchantTimestamp = merchantTransactionDto.getMerchantTimestamp();
    }
}
