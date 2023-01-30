package com.qbrainx.common.identity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public abstract class BaseDTO implements Serializable {

    private static final long serialVersionUID = 5617088966663603291L;

    private Long pkId;

    @JsonIgnore
    private LocalDateTime deletedAt;
}
