package com.qbrainx.common.mapparent;

import java.util.List;

import com.qbrainx.common.rest.MappedParentRef;

public class Address {

    private String city;
    private String district;
    private User user1;
    private Person person;
    @MappedParentRef(mappedBy = "address")
    private List<Country> country;

    public void setCountry(List<Country> country) {
        this.country = country;
    }


    public List<Country> getCountry() {
        return country;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Address() {

    }
    public Address(User user) {
        this.user1 = user;
    }

    public Address(String city, String district, User user) {
        this(user);
        this.city = city;
        this.district = district;
    }

    public User getUser() {
        return user1;
    }

    public void setUser(User user) {
        this.user1 = user;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

}
