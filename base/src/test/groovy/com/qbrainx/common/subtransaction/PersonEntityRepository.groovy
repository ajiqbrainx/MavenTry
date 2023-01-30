package com.dijta.common.subtransaction

import com.dijta.common.multitenant.MultiTenantEntityRepository
import org.springframework.stereotype.Repository

@Repository
interface PersonEntityRepository extends MultiTenantEntityRepository<PersonEntity>{

}