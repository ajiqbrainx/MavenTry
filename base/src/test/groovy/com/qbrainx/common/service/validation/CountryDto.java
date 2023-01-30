package com.qbrainx.common.service.validation;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.qbrainx.common.auditing.BaseAuditingDTO;
import com.qbrainx.common.validation.CustomLength;
import com.qbrainx.common.validation.CustomNonNull;

//@Validated
public class CountryDto extends BaseAuditingDTO {

    @CustomNonNull(fieldCode = "code", fieldDefaultValue = "CountryCode")
    @CustomLength(min = 2, max = 2, fieldCode = "code", fieldDefaultValue = "CountryCodeLength")
    //@Length(min = 2, max = 2)
    //@NotNull
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
