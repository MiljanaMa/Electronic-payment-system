package com.webshop.webshop_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micrometer.common.lang.NonNull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true)
    private String id;

    @Column(name = "username", unique = true)
    @NonNull
    private String username;

    @NonNull
    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "firstName")
    @NonNull
    private String firstName;

    @Column(name = "lastName")
    @NonNull
    private String lastName;

    @NonNull
    @JsonIgnore
    private String password;

}