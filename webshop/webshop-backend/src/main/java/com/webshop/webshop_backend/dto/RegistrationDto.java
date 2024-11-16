package com.webshop.webshop_backend.dto;

import com.webshop.webshop_backend.mapper.DtoEntity;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationDto implements DtoEntity {
    @NotEmpty(message = "Username is required")
    private String username;
    @NotEmpty(message = "First name is required")
    private String firstName;
    @NotEmpty(message = "Last name is required")
    private String lastName;
    @NotEmpty(message = "Password is required")
    private String password;
}
