package com.dijta.common.transaction

import com.dijta.common.EnableDijtaBase
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
@EnableDijtaBase
class IdentityGeneratorInsideTransactionalApp {

    static void main(String[] args) {
        SpringApplication.run(IdentityGeneratorInsideTransactionalApp.class, args)
    }
}