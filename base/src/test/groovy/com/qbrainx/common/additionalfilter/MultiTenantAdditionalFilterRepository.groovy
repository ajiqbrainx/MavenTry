package com.dijta.common.additionalfilter

import com.dijta.common.multitenant.MultiTenantEntityRepository
import org.springframework.stereotype.Repository

@Repository
interface MultiTenantAdditionalFilterRepository extends MultiTenantEntityRepository<PersonEntity> {}