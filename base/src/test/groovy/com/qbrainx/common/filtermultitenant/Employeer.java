package com.qbrainx.common.filtermultitenant;


import javax.persistence.Entity;

import com.qbrainx.common.multitenant.MultiTenantEntity;

@Entity
public class Employeer extends MultiTenantEntity {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
