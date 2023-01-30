package com.dijta.common.additionalfilter

import com.dijta.common.identity.PaginationRequest
import com.google.common.collect.ImmutableList

interface MultiTenantAdditionalFilterService {
    ImmutableList<PersonEntity> findAll()

    ImmutableList<PersonEntity> findAll(final PaginationRequest paginationRequest)

}