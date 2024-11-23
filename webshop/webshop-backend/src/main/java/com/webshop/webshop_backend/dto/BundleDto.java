package com.webshop.webshop_backend.dto;


import com.webshop.webshop_backend.mapper.DtoEntity;
import com.webshop.webshop_backend.model.BundleProduct;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BundleDto implements DtoEntity {

    private String id;

    @NotEmpty(message = "Name is required")
    private String name;

    @NotEmpty(message = "Description is required")
    private String description;

    @NotEmpty(message = "Price is required")
    private double price;
    private Set<BundleProductDto> products;
}

