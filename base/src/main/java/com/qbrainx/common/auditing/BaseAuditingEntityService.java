package com.qbrainx.common.auditing;

import com.qbrainx.common.identity.BaseEntityService;

public interface BaseAuditingEntityService<V extends BaseAuditingDTO, T extends BaseAuditingEntity> extends BaseEntityService<V, T> {
}
