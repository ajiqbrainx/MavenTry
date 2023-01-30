package com.dijta.common.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import spock.lang.Specification
import spock.lang.Unroll

class SecurityExpressionCheckerSpec extends Specification {

    @Unroll
    def 'Given Authentication check whether permitted or not #expression'() {
        given:
        Authentication authentication = Mock()
        authentication.principal >> "Test Subject"
        authentication.credentials >> "*******"
        authentication.authorities >> ["FEATURE_ONE", "ROLE_ONE"].collect { String s -> new SimpleGrantedAuthority(s) }

        when:
        Boolean result = new SecurityExpressionChecker().check(expression, authentication)

        then:
        result == expected

        where:
        expression                                      | expected
        "hasRole('ONE')"                                | true
        "hasRole('TWO')"                                | false
        "hasAuthority('FEATURE_ONE')"                   | true
        "hasAuthority('FEATURE_TWO')"                   | false
        "hasRole('ONE') && hasAuthority('FEATURE_ONE')" | true
        "hasRole('TWO') || hasAuthority('FEATURE_ONE')" | true
        "hasRole('TWO') && hasAuthority('FEATURE_ONE')" | false
        "'Test Subject' == authentication.principal"    | true
        "'Test Subject2' == authentication.principal"    | false


    }
}
