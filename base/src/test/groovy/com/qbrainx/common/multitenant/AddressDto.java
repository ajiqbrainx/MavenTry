package com.qbrainx.common.multitenant;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.qbrainx.common.multitenant.MulitTenantDTO;
import com.qbrainx.common.rest.MappedParentRef;

import java.util.List;

public class AddressDto extends MulitTenantDTO {

    private String city;
    private String district;

    @MappedParentRef(mappedBy = "address")
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
