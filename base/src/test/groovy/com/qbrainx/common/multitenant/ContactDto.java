package com.qbrainx.common.multitenant;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.qbrainx.common.multitenant.MulitTenantDTO;

public class ContactDto extends MulitTenantDTO {

    private Long mobileNumber;
    private String email;

    @JsonBackReference
    private EmployeeDTO employee;

    public Long getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(Long mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public EmployeeDTO getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeDTO employee) {
        this.employee = employee;
    }

}
