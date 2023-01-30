package com.dijta.common.security

import org.springframework.http.HttpHeaders
import spock.lang.Specification
import spock.lang.Unroll

class BearerTokensSpec extends Specification {


    def 'token type identification'() {
        given:
        def authToken = BearerToken.buildAuthTokenFromHeader({ s -> if (s == HttpHeaders.AUTHORIZATION) bearer else null })
        expect:
        authToken.getBearerToken() == expected

        where:
        bearer | expected
        "test" | "test"
        null   | null
    }

    @Unroll
    def 'build http headers'() {
        given:
        BearerToken authToken = BearerToken.builder()
                .bearerToken(bearer)
                .build()

        when:
        def headers = authToken.buildPropagationHeaders()

        then:
        headers.containsKey(HttpHeaders.AUTHORIZATION) == (bearer != null)
        headers.getFirst(HttpHeaders.AUTHORIZATION) == bearer

        where:
        bearer | bearerToken
        "test" | null
        null   | "token"

    }


}
