package com.qbrainx.common.auditing;

import com.qbrainx.common.identity.BaseEntityRepository;

public interface BaseAuditingEntityRepository<T extends BaseAuditingEntity> extends BaseEntityRepository<T> {
}
