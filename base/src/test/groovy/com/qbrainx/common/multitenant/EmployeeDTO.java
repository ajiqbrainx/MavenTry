package com.qbrainx.common.multitenant;

import java.util.List;

import com.qbrainx.common.multitenant.MulitTenantDTO;
import com.qbrainx.common.rest.MappedParentRef;

public class EmployeeDTO extends MulitTenantDTO {

    private String name;

    private boolean isActive;

    @MappedParentRef(mappedBy = "employee")
    private List<AddressDto> address;

    @MappedParentRef(mappedBy = "employee")
    private ContactDto contact;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean getIsActive(){return isActive;}

    public void setIsActive(final boolean isActiveBol) {
        this.isActive = isActiveBol;
    }

    public List<AddressDto> getAddress() {
        return address;
    }

    public void setAddress(List<AddressDto> address) {
        this.address = address;
    }

    public ContactDto getContact() {
        return contact;
    }

    public void setContact(ContactDto contact) {
        this.contact = contact;
    }

}
