package com.qbrainx.common.dijtasequence.transaction;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.qbrainx.common.identity.BaseEntity;
import com.qbrainx.common.identity.CustomSequence;

@Entity
public class UserEntity extends BaseEntity {

  @CustomSequence(tableName = "user_entity")
  private Long sequence;

  private String name;

  @OneToOne(cascade = CascadeType.ALL)
  private AddressEntity address;

  @OneToMany(cascade = CascadeType.ALL)
  private List<ContactEntity> contactList;

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

  public AddressEntity getAddress() {
    return address;
  }

  public void setAddress(AddressEntity address) {
    this.address = address;
  }

  public List<ContactEntity> getContactList() {
    return contactList;
  }

  public void setContactList(List<ContactEntity> contactList) {
    this.contactList = contactList;
  }

  @Override
  public String toString() {
    return "UserEntity{" +
            "sequence=" + sequence +
            ", name='" + name + '\'' +
            ", address=" + address +
            ", contactList=" + contactList + // + contactList != null && !contactList.isEmpty() ? contactList.get(0).getContactId().toString() : contactList +
            ", pkId=" + pkId +
            '}';
  }

}
