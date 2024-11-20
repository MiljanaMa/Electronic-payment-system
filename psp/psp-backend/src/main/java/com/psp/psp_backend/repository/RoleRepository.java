package com.psp.psp_backend.repository;

import com.psp.psp_backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {
    Role findByName(String name);
}