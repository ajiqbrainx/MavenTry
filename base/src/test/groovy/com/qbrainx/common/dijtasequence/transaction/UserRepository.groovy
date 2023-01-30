package com.dijta.common.dijtasequence.transaction

import com.dijta.common.identity.BaseEntityRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository extends BaseEntityRepository<UserEntity> {
}