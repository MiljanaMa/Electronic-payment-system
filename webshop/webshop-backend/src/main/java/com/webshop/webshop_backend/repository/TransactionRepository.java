package com.webshop.webshop_backend.repository;

import com.webshop.webshop_backend.model.BundleProduct;
import com.webshop.webshop_backend.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    @Query("SELECT t FROM Transaction t WHERE t.user.id = ?1")
    Set<Transaction> findTransactionsByUser(String userId);

}
