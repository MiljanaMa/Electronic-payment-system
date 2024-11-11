package com.webshop.webshop_backend.repository;

import com.webshop.webshop_backend.model.Product;
import com.webshop.webshop_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
