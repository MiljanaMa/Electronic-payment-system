package com.webshop.webshop_backend.repository;

import com.webshop.webshop_backend.model.Bundle;
import com.webshop.webshop_backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface BundleRepository extends JpaRepository<Bundle, String> {
    Optional<Bundle> findByName(String name);
    @Query("SELECT b FROM Bundle b")
    Set<Bundle> findAllBundles();
}