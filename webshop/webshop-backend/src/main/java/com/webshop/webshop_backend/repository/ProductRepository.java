package com.webshop.webshop_backend.repository;

import com.webshop.webshop_backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    Optional<Product> findByName(String name);
    @Query("SELECT p FROM Product p")
    Set<Product> findAllProducts();
}