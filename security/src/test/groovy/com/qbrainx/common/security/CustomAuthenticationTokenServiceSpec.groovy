package com.qbrainx.common.security

import io.vavr.collection.List
import org.springframework.security.core.GrantedAuthority
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

class CustomAuthenticationTokenServiceSpec extends Specification implements TestConstants {

    CustomAuthenticationTokenService customAuthenticationTokenService
    CustomSecurityConfigProperties customSecurityConfigProperties = CustomSecurityConfigProperties.builder().excludePaths(List.of("/opendata/**")).entrySecurityExpression("hasAuthority('TEST_APP_ALLOWED')").build()

    def setup() {
        customAuthenticationTokenService = new CustomAuthenticationTokenService(customSecurityConfigProperties, new SecurityExpressionChecker())
    }


    BearerToken authTokens(String bearerToken) {
        return BearerToken.builder()
                .bearerToken("Bearer $bearerToken")
                .build()
    }


    def 'test security with authorized and cookies propagated to dijta'() {
        given:
        BearerToken authTokens = authTokens(BEARER_TOKEN)

        when:
        def authenticationToken = customAuthenticationTokenService.getAuthentication(authTokens)

        then:
        authenticationToken != null
        authenticationToken.getCredentials() == authTokens
        authenticationToken.getPrincipal().id == 1
        authenticationToken.getAuthorities().collect { GrantedAuthority g -> g.getAuthority() } == ["TEST_APP_ALLOWED"]

    }


}
