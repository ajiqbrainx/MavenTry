package com.qbrainx.common.filtermultitenant;


import com.qbrainx.common.multitenant.MulitTenantDTO;

public class EmployeerDTO extends MulitTenantDTO {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
