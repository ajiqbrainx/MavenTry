package com.qbrainx.common.dijtasequence.transaction;

import com.qbrainx.common.identity.BaseDTO;
import com.qbrainx.common.rest.MappedParentRef;
import com.qbrainx.common.validation.CustomNonNull;

import java.util.List;
import javax.validation.Valid;

public class UserDto extends BaseDTO {

    private Long sequence;

    @CustomNonNull(fieldCode = "USER_NAME", fieldDefaultValue = "User Name should not null")
    private String name;

    @MappedParentRef(mappedBy = "user")
    @Valid
    private AddressDto address;

    @MappedParentRef(mappedBy = "user")
    @Valid
    private List<ContactDto> contactList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    public List<ContactDto> getContactList() {
        return contactList;
    }

    public void setContactList(List<ContactDto> contactList) {
        this.contactList = contactList;
    }

    /*@Override
    public String toString() {
        return "UserDto{" +
                "sequence=" + sequence +
                ", name='" + name + '\'' +
                ", address=" + address +
                ", contactList=" + contactList + // + contactList != null && !contactList.isEmpty() ? contactList.get(0).getContactId().toString() : contactList +
                ", pkId=" + getPkId() +
                '}';
    }*/
}
