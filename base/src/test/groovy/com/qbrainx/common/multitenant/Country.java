package com.qbrainx.common.multitenant;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.Where;

import com.qbrainx.common.multitenant.MultiTenantEntity;

@Entity
@Where(clause = "deleted_at is null")
public class Country  extends MultiTenantEntity {

    private String code;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_pk_id", referencedColumnName = "pk_id")
    private Address address;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

}
