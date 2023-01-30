package com.qbrainx.common.identity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public class BaseEntity {

    @Id
    @GenericGenerator(name = CustomIdGenerator.ID_TABLE, strategy = CustomIdGenerator.ID_GENERATOR)
    @GeneratedValue(generator = CustomIdGenerator.ID_TABLE)
    @Column(name = "pk_id")
    protected Long pkId;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

}
