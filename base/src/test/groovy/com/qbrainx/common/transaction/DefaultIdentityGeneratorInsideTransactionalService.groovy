package com.dijta.common.transaction


import com.google.common.collect.ImmutableList
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DefaultIdentityGeneratorInsideTransactionalService implements IdentityGeneratorInsideTransactionalService {

    private final IdentityGeneratorInsideTransactionalRepository repository

    DefaultIdentityGeneratorInsideTransactionalService(IdentityGeneratorInsideTransactionalRepository repository) {
        this.repository = repository
    }

    @Override
    @Transactional(readOnly = true)
    ImmutableList<PersonEntity> findAll() {
        return repository.findAll().stream().collect(ImmutableList.toImmutableList())
    }

    @Override
    @Transactional
    PersonEntity save(PersonEntity entity) {
        def save = repository.save(entity)
        return save
    }
}