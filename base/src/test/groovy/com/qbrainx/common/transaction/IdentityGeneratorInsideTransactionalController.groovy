package com.dijta.common.transaction


import com.google.common.collect.ImmutableList
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class IdentityGeneratorInsideTransactionalController {

    private final IdentityGeneratorInsideTransactionalService service

    IdentityGeneratorInsideTransactionalController(IdentityGeneratorInsideTransactionalService service) {
        this.service = service
    }

    @GetMapping(path = "/identity-generator-inside")
    ImmutableList<PersonEntity> findAll() {
        service.findAll()
    }
}
