package com.qbrainx.common.mapparent;

import com.qbrainx.common.rest.MappedParentRef;

public class User {

    private Long id;
    private String firstName;
    private String LastName;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @MappedParentRef(mappedBy = "user1")
    private Address address;

    public  User() {

    }

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

}
