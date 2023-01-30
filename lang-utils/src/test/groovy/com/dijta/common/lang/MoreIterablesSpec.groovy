package com.dijta.common.lang

import com.dijta.common.exception.RequestValidationException
import com.dijta.common.message.MessageCode
import org.springframework.http.HttpStatus
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Unit test specifications for the {@link MoreIterables} class.
 *
 */
class MoreIterablesSpec extends Specification {

    private static final String ELEMENT = "element"

    @Unroll
    def "notEmpty(Iterable) returns argument when not null or empty"() {
        given:
        List<String> list = ["one", "two", "three"]
        final Set<String> set = list as Set

        expect:
        MoreIterables.notEmpty(list, MessageCode.error("list")) == list
        MoreIterables.notEmpty(set, MessageCode.error("set")) == set
    }

    @Unroll
    def "notEmpty(Iterable) throws exception when argument is #state"() {
        when:
        MoreIterables.notEmpty(argument, MessageCode.error("argumentName"))

        then:
        final RequestValidationException e = thrown()
        e.getStatus() == HttpStatus.BAD_REQUEST
        e.getMessageCode() == MessageCode.error("argumentName")

        where:
        state        | argument
        "null"       | null as List<String>
        "empty list" | Collections.emptyList()
        "empty set"  | Collections.emptySet()
    }

}
