package com.psp.psp_backend.model;

import com.psp.psp_backend.dto.MerchantSubscriptionDto;
import com.psp.psp_backend.dto.MerchantTransactionDto;
import com.psp.psp_backend.model.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Entity
@Table(name = "subscriptions", uniqueConstraints = {
        @UniqueConstraint(columnNames = "id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true)
    private String id;
    @Column(name = "amount")
    private double amount;
    @Column(name = "startDate")
    private LocalDate startDate;
    @Column(name = "endDate")
    private LocalDate endDate;
    @Column(name = "status")
    private SubscriptionStatus status;
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    @Column(name = "client_subscription_id")
    private  String merchantSubscriptionId;
    @Column(name = "provider_subscription_id")
    private  String providerSubscriptionId;
    @Column(name = "product_description")
    private  String productDescription;
    @Column(name = "product_name")
    private  String productName;
    @Column(name = "product_id")
    private  String productId;

    public Subscription(MerchantSubscriptionDto merchantSubscriptionDto, Client client) {
        this.client = client;
        this.amount = merchantSubscriptionDto.getAmount();
        this.merchantSubscriptionId = merchantSubscriptionDto.getMerchantSubscriptionId();
        this.status = SubscriptionStatus.PENDING;
        this.startDate = merchantSubscriptionDto.getMerchantTimestamp();
        this.endDate = merchantSubscriptionDto.getMerchantTimestamp().plusYears(1);
        this.productDescription = merchantSubscriptionDto.getProductDescription();
        this.productName = merchantSubscriptionDto.getProductName();
        this.productId = merchantSubscriptionDto.getProductId();
    }
}
