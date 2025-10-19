package com.webshop.webshop_backend.repository;

import com.webshop.webshop_backend.model.Product;
import com.webshop.webshop_backend.model.Subscription;
import com.webshop.webshop_backend.model.User;
import com.webshop.webshop_backend.model.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface SubscriptionRepository extends JpaRepository<Subscription, String> {
    Optional<Subscription> findByUserAndPurchaseIdAndStatus(User user, String purchaseId, SubscriptionStatus status);
}
