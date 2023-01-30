package com.qbrainx.common.dijtasequence.transaction;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.qbrainx.common.identity.BaseEntity;
import com.qbrainx.common.identity.CustomSequence;


@Entity
public class AddressEntity extends BaseEntity {

    @CustomSequence(tableName = "address_entity", initialValue = 1000L)
    private Long sequence;

    private String city;

    private Long pin;

    @OneToOne//(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_pk_id", referencedColumnName = "pk_id")
    private UserEntity user;

    @OneToMany(cascade = CascadeType.ALL)
    private List<TownEntity> townList;

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

    public Long getPin() {
        return pin;
    }

    public void setPin(Long pin) {
        this.pin = pin;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public List<TownEntity> getTownList() {
        return townList;
    }

    public void setTownList(List<TownEntity> townList) {
        this.townList = townList;
    }

    @Override
    public String toString() {
        return "AddressEntity{" +
                "sequence=" + sequence +
                ", city='" + city + '\'' +
                ", pin=" + pin +
               // ", user=" + user +
                ", townList=" + townList +
                ", pkId=" + pkId +
                '}';
    }
}
