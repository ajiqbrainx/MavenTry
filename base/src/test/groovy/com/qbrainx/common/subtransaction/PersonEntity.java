package com.qbrainx.common.subtransaction;

import com.qbrainx.common.identity.CustomSequence;
import com.qbrainx.common.multitenant.MultiTenantEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class PersonEntity extends MultiTenantEntity {


    private String name;

    @Column(name = "sequence", updatable = false)
    @CustomSequence(incrementValue = 1, initialValue = 1000, tableName = "person_entity")
    private Long sequence;

    @OneToMany(mappedBy = "personEntity", cascade = CascadeType.ALL)
    private List<AddressEntity> addressEntityList;

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

    public List<AddressEntity> getAddressEntityList() {
        return addressEntityList;
    }

    public void setAddressEntityList(final List<AddressEntity> addressEntityList) {
        this.addressEntityList = addressEntityList;
    }
}
