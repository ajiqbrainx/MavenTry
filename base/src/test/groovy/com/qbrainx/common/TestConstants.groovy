package com.dijta.common

import com.dijta.common.security.SecurityPrincipal
import io.vavr.API

trait TestConstants {

    static String asJwt(String previlege, String overAllPrevilage = "TEST_APP_ALLOWED") {
        return SecurityPrincipal.builder().id(1L).name("Test User").privileges(API.Set(previlege, overAllPrevilage)).build().toJwt()
    }

    String asJwtWithTenant(String previlege, Long tenantId, String overAllPrevilage = "TEST_APP_ALLOWED") {
        return SecurityPrincipal
                .builder()
                .id(1L)
                .name("Test User")
                .tenantId(tenantId)
                .privileges(API.Set(previlege, overAllPrevilage))
                .build()
                .toJwt()
    }
}
