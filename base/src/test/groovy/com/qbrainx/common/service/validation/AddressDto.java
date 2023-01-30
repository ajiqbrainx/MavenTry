package com.qbrainx.common.service.validation;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.qbrainx.common.auditing.BaseAuditingDTO;
import com.qbrainx.common.rest.MappedParentRef;

import java.util.List;
import javax.validation.Valid;
import org.springframework.validation.annotation.Validated;

@Validated
public class AddressDto extends BaseAuditingDTO {

    //@DijtaNonNull(fieldCode = "city_Code", fieldDefaultValue = "city Value")
    private String city;

    private String district;
    @MappedParentRef(mappedBy = "address")
    @Valid
    private List<CountryDto> country;

    @JsonBackReference
    private EmployeeDTO employee;

    public List<CountryDto> getCountry() {
        return country;
    }

    public void setCountry(List<CountryDto> country) {
        this.country = country;
    }


    public EmployeeDTO getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeDTO employee) {
        this.employee = employee;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

}
