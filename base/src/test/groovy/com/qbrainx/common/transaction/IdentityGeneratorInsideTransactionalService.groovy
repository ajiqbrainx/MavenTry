package com.dijta.common.transaction


import com.google.common.collect.ImmutableList

interface IdentityGeneratorInsideTransactionalService {
    ImmutableList<PersonEntity> findAll()

    PersonEntity save(PersonEntity entity)
}