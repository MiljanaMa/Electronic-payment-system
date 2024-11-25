package com.psp.psp_backend.repository;

import com.psp.psp_backend.model.Client;
import com.psp.psp_backend.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    @Query("SELECT t FROM Transaction t WHERE t.merchantTransactionId = :merchantOrderId")
    Optional<Transaction> findByMerchantOrderId(@Param("merchantOrderId") String merchantOrderId);
}
