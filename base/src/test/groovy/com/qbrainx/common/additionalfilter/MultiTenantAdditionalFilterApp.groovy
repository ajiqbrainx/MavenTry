package com.dijta.common.additionalfilter

import com.dijta.common.EnableDijtaBase
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
@EnableDijtaBase
class MultiTenantAdditionalFilterApp {

    static void main(String[] args) {
        SpringApplication.run(MultiTenantAdditionalFilterApp.class, args)
    }
}