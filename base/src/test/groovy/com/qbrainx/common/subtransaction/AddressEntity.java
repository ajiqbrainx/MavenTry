package com.qbrainx.common.subtransaction;

import com.qbrainx.common.identity.CustomSequence;
import com.qbrainx.common.multitenant.MultiTenantEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class AddressEntity extends MultiTenantEntity {

    private String name;

    @CustomSequence(incrementValue = 1, initialValue = 1000, tableName = "address_entity")
    private Long sequence;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_pk_id", referencedColumnName = "pk_id")
    private PersonEntity personEntity;

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

    public PersonEntity getPersonEntity() {
        return personEntity;
    }

    public void setPersonEntity(final PersonEntity personEntity) {
        this.personEntity = personEntity;
    }
}
