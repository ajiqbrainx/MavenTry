package com.qbrainx.common.multitenant;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.hibernate.annotations.Where;

import com.qbrainx.common.multitenant.MultiTenantEntity;

@Entity
@Where(clause = "deleted_at is null")
public class Employee extends MultiTenantEntity {

    private String name;

    private boolean isActive;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Address> address;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL)
    private Contact contact;

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

    public List<Address> getAddress() {
        return address;
    }

    public void setAddress(List<Address> address) {
        this.address = address;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

}
