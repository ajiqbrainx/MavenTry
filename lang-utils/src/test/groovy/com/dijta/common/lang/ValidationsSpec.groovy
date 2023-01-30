package com.dijta.common.lang

import com.dijta.common.exception.RequestValidationException
import com.dijta.common.message.MessageCode
import org.springframework.http.HttpStatus
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Unit test specification for the {@link Validations} class.
 *
 */
class ValidationsSpec extends Specification {

    private static final String EMPTY_STRING = ""
    private static final String WHITESPACE_STRING = "  "
    private static final String NON_NULL_STRING = "test.string"
    private static final Integer NON_NULL_INTEGER = 123
    private static final MessageCode DESCRIPTION = MessageCode.error("test.description")
    private static final MessageCode OTHER_DESCRIPTION = MessageCode.error("other.description")

    def "notNull() should return non-null arguments as result with String argument"() {
        when:
        final String actualResult = Validations.notNull(NON_NULL_STRING, DESCRIPTION)

        then:
        actualResult == NON_NULL_STRING
    }

    def "notNull() should return non-null arguments as result with Integer argument"() {
        when:
        final Integer actualResult = Validations.notNull(NON_NULL_INTEGER, OTHER_DESCRIPTION)

        then:
        actualResult == NON_NULL_INTEGER
    }

    def "notNull() should throw expected exception when String argument is null"() {
        when:
        Validations.notNull(null as String, DESCRIPTION)

        then:
        final RequestValidationException e = thrown()
        e.getMessageCode() == DESCRIPTION
        e.getStatus() == HttpStatus.BAD_REQUEST
    }

    def "notNull() should throw expected exception when Integer argument is null"() {
        when:
        Validations.notNull(null as Integer, DESCRIPTION)

        then:
        final RequestValidationException e = thrown()
        e.getMessageCode() == DESCRIPTION
        e.getStatus() == HttpStatus.BAD_REQUEST
    }


    def "notNull() with function argument should return non-null arguments as result with String argument"() {
        when:
        final String actualResult =
            Validations.notNull(NON_NULL_STRING, DESCRIPTION, { new RequestValidationException(it) });

        then:
        actualResult == NON_NULL_STRING
    }

    def "notNull() with function argument should return non-null arguments as result with Integer argument"() {
        when:
        final Integer actualResult =
            Validations.notNull(NON_NULL_INTEGER, OTHER_DESCRIPTION, { new RequestValidationException(it) })

        then:
        actualResult == NON_NULL_INTEGER
    }

    def "notNull() with function argument should throw expected exception when String argument is null"() {
        when:
        Validations.notNull(null as String, DESCRIPTION, { new RequestValidationException(it) })

        then:
        final RequestValidationException e = thrown()
        e.getMessageCode() == DESCRIPTION
        e.getStatus() == HttpStatus.BAD_REQUEST
    }

    def "notNull() with function argument should throw expected exception when Integer argument is null"() {
        when:
        Validations.notNull(null as Integer, DESCRIPTION, { new RequestValidationException(it) })

        then:
        final RequestValidationException e = thrown()
        e.getMessageCode() == DESCRIPTION
        e.getStatus() == HttpStatus.BAD_REQUEST
    }


    @Unroll
    def "notBlank() should return valid argument as result for #input"() {
        when:
        final String actualResult = Validations.notBlank(input, DESCRIPTION)

        then:
        actualResult == input

        where:
        input << ["hello", "goodbye"]
    }

    @Unroll
    def "notBlank() should throw expected exception when value is #state"() {
        when:
        Validations.notBlank(value, DESCRIPTION)

        then:
        final RequestValidationException e = thrown()
        e.getMessageCode() == DESCRIPTION
        e.getStatus() == HttpStatus.BAD_REQUEST

        where:
        state        | value
        'null'       | null
        'empty'      | EMPTY_STRING
        'whitespace' | WHITESPACE_STRING
    }


    @Unroll
    def "notBlank() with function argument should return valid argument as result for #input"() {
        when:
        final String actualResult = Validations.notBlank(input, DESCRIPTION, { new RequestValidationException(it) })

        then:
        actualResult == input

        where:
        input << ["hello", "goodbye"]
    }

    @Unroll
    def "notBlank() with function argument should throw expected exception when value is #state"() {
        when:
        Validations.notBlank(value, DESCRIPTION, { new RequestValidationException(it) })

        then:
        final RequestValidationException e = thrown()
        e.getMessageCode() == DESCRIPTION
        e.getStatus() == HttpStatus.BAD_REQUEST

        where:
        state        | value
        'null'       | null
        'empty'      | EMPTY_STRING
        'whitespace' | WHITESPACE_STRING
    }

}
