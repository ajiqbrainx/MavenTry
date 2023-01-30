package com.qbrainx.common.auditing;

import com.qbrainx.common.auditing.BaseAuditingDTO;

public class PersonDTO extends BaseAuditingDTO {

    private Long sequence;
    private String name;

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

    @Override
    public String toString() {

        return "{" +
                "\"pkId\":" + getPkId() +
                ",\"createdAt\":" + getCreatedAt() +
                ",\"createdBy\":" + getCreatedBy() +
                ",\"changedAt\":" + getChangedAt() +
                ",\"changedBy\":" + getChangedBy() +
                ",\"sequence\":" + sequence +
                ",\"name\":" + name + '\'' +
                ",\"deletedAt\":"+ getDeletedAt() +
                '}';
    }
}
