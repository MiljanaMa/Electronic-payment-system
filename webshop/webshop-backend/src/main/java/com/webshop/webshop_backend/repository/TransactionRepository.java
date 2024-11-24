package com.webshop.webshop_backend.repository;

import com.webshop.webshop_backend.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

}
