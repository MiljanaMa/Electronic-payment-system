package com.webshop.webshop_backend.dto;


import com.webshop.webshop_backend.mapper.DtoEntity;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto implements DtoEntity {

    private String id;

    @NotEmpty(message = "Product type is required")
    private String productType;

    @NotEmpty(message = "Name is required")
    private String name;

    @NotEmpty(message = "Description is required")
    private String description;

    @NotEmpty(message = "Price is required")
    private double price;
}
