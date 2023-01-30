package com.qbrainx.common.additionalfilter;

import org.hibernate.annotations.Where;

import com.qbrainx.common.multitenant.MultiTenantEntity;

import javax.persistence.Entity;

@Entity
@Where(clause = "deleted_at is null")
public class PersonEntity extends MultiTenantEntity {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
