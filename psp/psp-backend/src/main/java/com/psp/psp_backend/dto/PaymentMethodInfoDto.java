package com.psp.psp_backend.dto;

import com.psp.psp_backend.mapper.DtoEntity;

public class PaymentMethodInfoDto implements DtoEntity {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
