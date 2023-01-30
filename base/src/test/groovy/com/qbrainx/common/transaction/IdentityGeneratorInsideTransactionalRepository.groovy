package com.dijta.common.transaction

import com.dijta.common.identity.BaseEntityRepository
import org.springframework.stereotype.Repository

@Repository
interface IdentityGeneratorInsideTransactionalRepository extends BaseEntityRepository<PersonEntity> {
}