package com.webshop.webshop_backend.repository;

import com.webshop.webshop_backend.model.Bundle;
import com.webshop.webshop_backend.model.BundleProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface BundleProductRepository extends JpaRepository<BundleProduct, String> {

    @Query("SELECT bp FROM BundleProduct bp WHERE bp.bundle.id = ?1")
    Set<BundleProduct> findBundleProductsByBundle(String bundleId);

}
