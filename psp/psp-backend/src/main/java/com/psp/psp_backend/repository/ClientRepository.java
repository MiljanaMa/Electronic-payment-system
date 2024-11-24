package com.psp.psp_backend.repository;

import com.psp.psp_backend.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
    @Query("SELECT c FROM Client c WHERE c.username = :username")
    Optional<Client> findByUsername(@Param("username") String username);
    @Query("SELECT c FROM Client c WHERE c.merchantId = :merchantId AND c.merchantPassword = :merchantPass")
    Optional<Client> findByMerchantIdAndPass(@Param("merchantId") String merchantId, @Param("merchantPass") String merchantPass);
    @Query("SELECT c FROM Client c WHERE c.merchantId = :merchantId")
    Optional<Client> findByMerchantId(@Param("merchantId") String merchantId);
}
