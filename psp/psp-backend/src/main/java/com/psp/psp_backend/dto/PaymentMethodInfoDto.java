package com.psp.psp_backend.dto;

import com.psp.psp_backend.mapper.DtoEntity;

public class PaymentMethodInfoDto implements DtoEntity {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
