package com.qbrainx.common.transaction;

import javax.persistence.Entity;

import com.qbrainx.common.identity.BaseEntity;
import com.qbrainx.common.identity.CustomSequence;

@Entity
public class PersonEntity extends BaseEntity {

  private String name;

  @CustomSequence(tableName = "person_entity")
  private Long sequence;


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
}
