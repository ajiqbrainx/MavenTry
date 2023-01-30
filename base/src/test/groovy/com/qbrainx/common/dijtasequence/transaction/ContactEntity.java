package com.qbrainx.common.dijtasequence.transaction;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.qbrainx.common.identity.BaseEntity;
import com.qbrainx.common.identity.CustomSequence;

@Entity
public class ContactEntity extends BaseEntity {
    @CustomSequence(tableName = "contact_entity", initialValue = 543000L, incrementValue = 50L)
    private Long contactId;

    private String email;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_pk_id", referencedColumnName = "pk_id")
    private UserEntity user;

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "ContactEntity{" +
                "pkId=" + pkId +
                ", contactId=" + contactId +
                ", email='" + email + '\'' +
                //", user=" + user +
                '}';
    }
}
