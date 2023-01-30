package com.qbrainx.common.security

import io.vavr.collection.List
import spock.lang.Specification

class CustomSecurityConfigPropertiesSpec extends Specification {

    def 'check exclude paths array logic'() {
        when:
        def builder = CustomSecurityConfigProperties.builder()
        println builder
        def props = builder.excludePaths(excludePaths as List<String>).build()

        then:
        props.getExcludePathAsArray() == expectedArray
        props.isExcludePathPresent() == expectedIsExcludePath

        where:
        excludePaths         | expectedArray | expectedIsExcludePath
        List.ofAll([])       | new String[0] | false
        List.ofAll(["test"]) | ["test"]      | true
        null                 | new String[0] | false
    }
}
