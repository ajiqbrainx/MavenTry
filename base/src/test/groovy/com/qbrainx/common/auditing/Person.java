package com.qbrainx.common.auditing;

import org.hibernate.annotations.Where;

import com.qbrainx.common.auditing.BaseAuditingEntity;

import javax.persistence.Entity;

@Entity
@Where(clause = "deleted_at is null")
public class Person extends BaseAuditingEntity {

    private String name;

    private boolean isActive;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

}
