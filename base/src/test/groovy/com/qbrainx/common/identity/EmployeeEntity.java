package com.qbrainx.common.identity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "employee")
public class EmployeeEntity extends BaseEntity {

  @Column(name = "sequence", updatable = false)
  @CustomSequence(tableName = "employee")
  private Long sequence;
  private String name;
  private String lastName;

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public Long getSequence() {
    return sequence;
  }

  public void setSequence(final Long sequence) {
    this.sequence = sequence;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(final String lastName) {
    this.lastName = lastName;
  }
}
