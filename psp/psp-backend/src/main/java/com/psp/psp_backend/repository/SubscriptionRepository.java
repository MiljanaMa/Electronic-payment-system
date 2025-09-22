package com.psp.psp_backend.repository;

import com.psp.psp_backend.model.Subscription;
import com.psp.psp_backend.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, String> {
    @Query("SELECT s FROM Subscription s WHERE s.merchantSubscriptionId = :merchantSubscriptionId")
    Optional<Subscription> findByMerchantSubscriptionId(@Param("merchantSubscriptionId") String merchantSubscriptionId);
}
