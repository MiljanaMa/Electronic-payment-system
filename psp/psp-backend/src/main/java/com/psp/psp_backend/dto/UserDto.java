package com.psp.psp_backend.dto;

import com.psp.psp_backend.mapper.DtoEntity;

public class UserDto implements DtoEntity {
    private String companyName;
    private String companyEmail;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }
}
