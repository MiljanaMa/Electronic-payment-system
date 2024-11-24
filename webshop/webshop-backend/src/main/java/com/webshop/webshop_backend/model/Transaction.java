package com.webshop.webshop_backend.model;

import com.webshop.webshop_backend.model.enums.TransactionStatus;
import com.webshop.webshop_backend.model.enums.TransactionType;
import jakarta.persistence.*;

import java.util.Date;

@Entity
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

    public Transaction() {
    }

    public Transaction(double amount, TransactionType type, User user, String purchaseId) {
        this.amount = amount;
        this.timestamp = new Date();
        this.status = TransactionStatus.PENDING;
        this.type = type;
        this.user = user;
        this.purchaseId = purchaseId;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(String purchaseId) {
        this.purchaseId = purchaseId;
    }
}
