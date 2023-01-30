package com.qbrainx.common.multitenant;

import org.springframework.stereotype.Repository;

import com.qbrainx.common.auditing.BaseAuditingEntityRepository;

@Repository
public interface MultiTenantEntityRepository<T extends MultiTenantEntity> extends BaseAuditingEntityRepository<T> {

}
