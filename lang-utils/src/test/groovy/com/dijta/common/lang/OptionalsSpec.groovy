package com.dijta.common.lang

import com.dijta.common.exception.RequestValidationException
import com.dijta.common.message.MessageCode
import org.springframework.http.HttpStatus
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

/**
 * Unit tests for the {@link Optionals} class.
 *
 */
class OptionalsSpec extends Specification {

    @Unroll
    def "from() returns correct result for String input of #input"() {
        expect:
        Optionals.from(input) == expectedResult

        where:
        input   | expectedResult
        'hello' | Optional.of('hello')
        null    | Optional.empty()
        ''      | Optional.empty()
    }

    def "from() returns correct result for Object input"() {
        given:
        final LocalDate localDate = LocalDate.now()
        final LocalDate nullLocalDate = null

        expect:
        Optionals.from(1) == Optional.of(1)
        Optionals.from(localDate) == Optional.of(localDate)
        Optionals.from(nullLocalDate) == Optional.empty()

    }

    def "checkPresent returns value of argument when candidate is present"() {
        expect:
        Optionals.checkPresent(Optional.of("one"), MessageCode.error("argumentName")) == "one"
        Optionals.checkPresent(Optional.of(1), MessageCode.error("argumentName")) == 1
    }

    def "checkPresent throws IllegalArgumentException when candidate is not present"() {
        when:
        Optionals.checkPresent(Optional.empty(), MessageCode.error("argumentName"))

        then:
        final RequestValidationException e = thrown()
        e.getStatus() == HttpStatus.BAD_REQUEST
    }

    def "checkPresent(IllegalStateException) returns value of argument when candidate is present"() {
        expect:
        Optionals.checkPresent(Optional.of("one"), MessageCode.error("argumentName"), { new IllegalStateException(it) }) == "one"
        Optionals.checkPresent(Optional.of(1), MessageCode.error("argumentName"), { new IllegalStateException(it) }) == 1
    }

    def "checkPresent(IllegalStateException) throws IllegalStateException when candidate is not present"() {
        when:
        Optionals.checkPresent(Optional.empty(), MessageCode.error("argumentName"), { new RequestValidationException(it) })

        then:
        final RequestValidationException e = thrown()
        e.getStatus() == HttpStatus.BAD_REQUEST
    }

}
