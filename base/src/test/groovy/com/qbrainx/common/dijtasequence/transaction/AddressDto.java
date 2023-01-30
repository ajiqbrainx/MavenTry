package com.qbrainx.common.dijtasequence.transaction;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.qbrainx.common.identity.BaseDTO;
import com.qbrainx.common.rest.MappedParentRef;
import com.qbrainx.common.validation.CustomLength;

import java.util.List;
import javax.validation.Valid;

public class AddressDto extends BaseDTO {

    private Long sequence;

    private String city;

    @CustomLength(min = 6, max = 10, fieldCode = "ADDRESS_PIN", fieldDefaultValue = "PIN Number length should be between 6 and 10")
    private String pin;

    @JsonBackReference
    private UserDto user;

    @MappedParentRef(mappedBy = "address")
    @Valid
    private List<TownDto> townList;

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public List<TownDto> getTownList() {
        return townList;
    }

    public void setTownList(List<TownDto> townList) {
        this.townList = townList;
    }

    /*@Override
    public String toString() {
        return "AddressDto{" +
                "sequence=" + sequence +
                ", city='" + city + '\'' +
                ", pin=" + pin +
                //", user=" + user +
                ", townList=" + townList +
                ", pkId=" + getPkId() +
                '}';
    }*/
}
