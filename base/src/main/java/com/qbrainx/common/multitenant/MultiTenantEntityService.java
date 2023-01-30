package com.qbrainx.common.multitenant;

import com.qbrainx.common.auditing.BaseAuditingEntityService;


public interface MultiTenantEntityService<V extends MulitTenantDTO, T extends MultiTenantEntity> extends BaseAuditingEntityService<V, T> {
}
