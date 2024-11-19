package com.webshop.webshop_backend.dto;
import com.webshop.webshop_backend.mapper.DtoEntity;
import com.webshop.webshop_backend.model.BundleProduct;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BundleProductDto {

    private String id;

    @NotEmpty(message = "Product is required")
    private ProductDto product;

    @NotEmpty(message = "Bundle is required")
    private BundleDto bundle;

}
