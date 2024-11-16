package com.webshop.webshop_backend.model;

import com.webshop.webshop_backend.model.enums.ProductType;
import io.micrometer.common.lang.NonNull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "productType")
    @NonNull
    private ProductType productType;

    @Column(name = "name", unique = true)
    @NonNull
    private String name;

    @Column(name = "description")
    @NonNull
    private String description;

    @Column(name = "price")
    @NonNull
    private double price;

}