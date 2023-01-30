package com.qbrainx.common.mapparent;

import java.util.List;

import com.qbrainx.common.rest.MappedParentRef;

public class Person {

    private Long id;
    private String firstName;
    private String LastName;
    @MappedParentRef(mappedBy = "person")
    private List<Address> addressList;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public List<Address> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }

}
