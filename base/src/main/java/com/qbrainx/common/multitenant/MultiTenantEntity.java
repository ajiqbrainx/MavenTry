package com.qbrainx.common.multitenant;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import com.qbrainx.common.auditing.BaseAuditingEntity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Getter
@Setter
@MappedSuperclass
@FilterDefs({
        @FilterDef(name = "tenantIdFilter", defaultCondition = "tenant_id = :tenantId", parameters = {
                @ParamDef(name = "tenantId", type = "long")
        }),
        @FilterDef(name = "tenantIdInFilter", defaultCondition = "tenant_id in (:tenantIds)", parameters = {
                @ParamDef(name = "tenantIds", type = "long")
        })
})

@Filters({
        @Filter(name = "tenantIdFilter"),
        @Filter(name = "tenantIdInFilter")
})

public class MultiTenantEntity extends BaseAuditingEntity {

    @Column(name = "tenant_id")
    private Long tenantId;
}
