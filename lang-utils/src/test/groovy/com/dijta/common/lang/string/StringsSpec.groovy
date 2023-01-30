package com.dijta.common.lang.string

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Unit test specification for the {@link Strings} class.
 *
 */
class StringsSpec extends Specification {

    @Unroll
    def "absentIfBlank returns correct result for input of #input"() {
        expect:
        Strings.absentIfBlank(input) == expectedResult

        where:
        input     | expectedResult
        'hello'   | Optional.of('hello')
        ' hello ' | Optional.of('hello')
        'goodbye' | Optional.of('goodbye')
        'a'       | Optional.of('a')
        null      | Optional.empty()
        ''        | Optional.empty()
        '    '    | Optional.empty()
        '\t'      | Optional.empty()
    }
}
