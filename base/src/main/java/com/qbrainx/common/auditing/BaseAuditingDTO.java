package com.qbrainx.common.auditing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.qbrainx.common.identity.BaseDTO;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class BaseAuditingDTO extends BaseDTO {

    private static final long serialVersionUID = 1476082742845891158L;

    @JsonProperty(access = Access.READ_ONLY)
    private LocalDateTime createdAt;
    @JsonProperty(access = Access.READ_ONLY)
    private Long createdBy;
    @JsonProperty(access = Access.READ_ONLY)
    private LocalDateTime changedAt;
    @JsonProperty(access = Access.READ_ONLY)
    private Long changedBy;

}
