package com.qbrainx.common.cache;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@javax.persistence.Entity
class Entity implements Serializable {

    private static final long serialVersionUID = 5148189545804925230L;

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private LocalDateTime updateTime;

    public static Entity of(final String theName) {
        final Entity entity = new Entity();
        entity.setName(theName);
        return entity;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(final LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}