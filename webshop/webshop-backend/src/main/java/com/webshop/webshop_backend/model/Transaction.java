package com.webshop.webshop_backend.model;

import com.webshop.webshop_backend.model.enums.TransactionStatus;
import com.webshop.webshop_backend.model.enums.TransactionType;
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
    @Column(name = "amount")
    private double amount;
    @Column(name = "timestamp")
    private Date timestamp;
    @Column(name = "status")
    private TransactionStatus status;
    @Column(name = "type")
    private TransactionType type;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "purchase_id")
    private  String purchaseId;

    public Transaction(double amount, TransactionType type, User user, String purchaseId) {
        this.amount = amount;
        this.timestamp = new Date();
        this.status = TransactionStatus.PENDING;
        this.type = type;
        this.user = user;
        this.purchaseId = purchaseId;
    }

}
