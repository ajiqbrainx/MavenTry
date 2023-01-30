package com.qbrainx.common.multitenant;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.qbrainx.common.multitenant.MulitTenantDTO;

public class CountryDto  extends MulitTenantDTO {

    private String code;

    @JsonBackReference
    private AddressDto address;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

}
