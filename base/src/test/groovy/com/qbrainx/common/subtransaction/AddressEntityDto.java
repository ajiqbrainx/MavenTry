package com.qbrainx.common.subtransaction;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.qbrainx.common.multitenant.MulitTenantDTO;

public class AddressEntityDto extends MulitTenantDTO {

    private static final long serialVersionUID = 6510926363373397215L;

    private String name;

    private Long sequence;

    @JsonBackReference
    private PersonEntityDto personEntity;

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

    public PersonEntityDto getPersonEntity() {
        return personEntity;
    }

    public void setPersonEntity(final PersonEntityDto personEntity) {
        this.personEntity = personEntity;
    }
}
