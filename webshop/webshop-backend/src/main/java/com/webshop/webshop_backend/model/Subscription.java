package com.webshop.webshop_backend.model;

import com.webshop.webshop_backend.model.enums.ProductType;
import com.webshop.webshop_backend.model.enums.SubscriptionStatus;
import com.webshop.webshop_backend.model.enums.TransactionStatus;
import com.webshop.webshop_backend.model.enums.TransactionType;
import io.micrometer.common.lang.NonNull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.Date;

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
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "purchase_id")
    private  String purchaseId;
    @ManyToOne
    @JoinColumn(name = "bundle_product_id", nullable = false)
    private BundleProduct bundleProduct;
}
