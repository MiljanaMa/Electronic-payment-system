package com.webshop.webshop_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.micrometer.common.lang.NonNull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Entity
@Table(name = "bundles", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bundle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true)
    private String id;

    @Column(name = "name", unique = true)
    @NonNull
    private String name;

    @Column(name = "description")
    @NonNull
    private String description;

    @Column(name = "price")
    @NonNull
    private double price;

    @Transient
    @JsonManagedReference
    @OneToMany(mappedBy = "bundle", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    Set<BundleProduct> products;
}