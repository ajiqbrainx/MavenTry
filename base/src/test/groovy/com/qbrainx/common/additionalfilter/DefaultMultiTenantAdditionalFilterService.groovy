package com.dijta.common.additionalfilter

import com.dijta.common.identity.PaginationRequest
import com.dijta.common.specifications.GenericSpecificationsBuilder
import com.google.common.collect.ImmutableList
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DefaultMultiTenantAdditionalFilterService implements MultiTenantAdditionalFilterService {

    private final MultiTenantAdditionalFilterRepository repository

    DefaultMultiTenantAdditionalFilterService(MultiTenantAdditionalFilterRepository repository) {
        this.repository = repository
    }

    @Override
    @Transactional(readOnly = true)
    ImmutableList<PersonEntity> findAll() {
        return repository.findAll().stream().collect(ImmutableList.toImmutableList())
    }

    @Override
    @Transactional(readOnly = true)
    ImmutableList<PersonEntity> findAll(final PaginationRequest paginationRequest) {
        final Specification<PersonEntity> specification = paginationRequest.getSearch()
                .map(this.&buildSpecification)
                .orElse(emptySpecification());
        return repository.findAll(specification).stream().collect(ImmutableList.toImmutableList())
    }

    public Specification<PersonEntity> buildSpecification(final String filter) {
        return GenericSpecificationsBuilder.<PersonEntity>builder().withFilter(filter).build();
    }

    private Specification<PersonEntity> emptySpecification() {
        return { root, query, criteriaBuilder -> null };
    }
}