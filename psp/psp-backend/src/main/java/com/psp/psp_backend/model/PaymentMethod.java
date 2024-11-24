package com.psp.psp_backend.model;

import jakarta.persistence.*;

import java.util.UUID;
@Entity
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;
    @Column(name = "name", unique = true, nullable = false)
    private String name;
    @Column(name = "api_url", unique = true, nullable = false)
    private String apiUrl;

    public PaymentMethod() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
