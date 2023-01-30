package com.qbrainx.common.dijtasequence.transaction;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.qbrainx.common.identity.BaseDTO;

public class TownDto extends BaseDTO {

    private Long townId;

    private String code;

    @JsonBackReference
    private AddressDto address;

    public Long getTownId() {
        return townId;
    }

    public void setTownId(Long townId) {
        this.townId = townId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    /*@Override
    public String toString() {
        return "TownDto{" +
                "pkId=" + getPkId() +
                ", townId=" + townId +
                ", code='" + code + '\'' +
                '}';
    }*/

}
