package com.qbrainx.common.dijtasequence.transaction;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.qbrainx.common.identity.BaseDTO;
import com.qbrainx.common.validation.CustomEmail;

public class ContactDto extends BaseDTO {

    private Long contactId;

    @CustomEmail(fieldCode = "EMAIL", fieldDefaultValue = "Email format abc@xyz.pqr")
    private String email;

    @JsonBackReference
    private UserDto user;

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

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

/*    @Override
    public String toString() {
        return "ContactDto{" +
                "pkId=" + getPkId() +
                ", contactId=" + contactId +
                ", email='" + email + '\'' +
                //", user=" + user +
                '}';
    }*/
}
