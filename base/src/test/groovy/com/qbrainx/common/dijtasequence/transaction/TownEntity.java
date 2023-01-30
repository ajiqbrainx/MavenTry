package com.qbrainx.common.dijtasequence.transaction;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.qbrainx.common.identity.BaseEntity;
import com.qbrainx.common.identity.CustomSequence;

@Entity
public class TownEntity extends BaseEntity {

    @CustomSequence(tableName = "town_entity", initialValue = 100L, incrementValue = 10L)
    private Long townId;

    private String code;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_pk_id", referencedColumnName = "pk_id")
    private AddressEntity address;

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

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "TownEntity{" +
                "pkId=" + pkId +
                ", townId=" + townId +
                ", code='" + code + '\'' +
                //", address=" + address +
                '}';
    }
}
