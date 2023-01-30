package com.dijta.common.additionalfilter

import com.dijta.common.identity.PaginationRequest
import com.google.common.collect.ImmutableList
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MultiTenantAdditionalFilterController {

    private final MultiTenantAdditionalFilterService service

    MultiTenantAdditionalFilterController(MultiTenantAdditionalFilterService service) {
        this.service = service
    }

    @GetMapping(path = "/multitenant-additional-filter")
    ImmutableList<PersonEntity> findAll() {
        service.findAll()
    }

    @GetMapping(path = "/multitenant-additional-filter-in")
    ImmutableList<PersonEntity> findAllIn(final PaginationRequest paginationRequest) {
        service.findAll(paginationRequest)
    }
}
