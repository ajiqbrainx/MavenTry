package com.qbrainx.common.subtransaction;

import com.qbrainx.common.multitenant.MulitTenantDTO;
import com.qbrainx.common.rest.MappedParentRef;

import java.util.List;

public class PersonEntityDto extends MulitTenantDTO {

    private static final long serialVersionUID = 5987212220219253795L;


    private String name;

    private Long sequence;

    @MappedParentRef(mappedBy = "personEntity")
    private List<AddressEntityDto> addressEntityList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public List<AddressEntityDto> getAddressEntityList() {
        return addressEntityList;
    }

    public void setAddressEntityList(final List<AddressEntityDto> addressEntityList) {
        this.addressEntityList = addressEntityList;
    }
}
