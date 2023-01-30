package com.dijta.common.lang

import com.dijta.common.exception.RequestValidationException
import com.dijta.common.message.MessageCode
import com.google.common.collect.*
import org.springframework.http.HttpStatus
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Unit test specifications for the {@link AdditionalCollections} class.
 *
 */
class AdditionalCollectionsSpec extends Specification {

    def "asImmutableList returns immutable copy of argument list"() {
        given:
        final List<Integer> sourceIntegerList = [1, 2, 3]
        final List<String> sourceStringList = ["one", "two", "three"]
        final Set<String> sourceStringSet = sourceStringList as Set

        expect:
        AdditionalCollections.asImmutableList(sourceIntegerList) == ImmutableList.of(1, 2, 3)
        AdditionalCollections.asImmutableList(sourceStringList) == ImmutableList.of("one", "two", "three")
        AdditionalCollections.asImmutableList(sourceStringSet) == ImmutableList.of("one", "two", "three")
        AdditionalCollections.asImmutableList([]) == ImmutableList.of()
    }

    def "asImmutableList returns empty list when passed null"() {
        when:
        final ImmutableList<String> actualResult = AdditionalCollections.asImmutableList(null as List<String>)

        then:
        actualResult == ImmutableList.of()
    }

    def "asImmutableSet returns immutable copy of argument set"() {
        given:
        final Set<Integer> sourceIntegerSet = [1, 2, 3] as Set
        final List<String> sourceStringList = ["one", "two", "three"]
        final Set<String> sourceStringSet = sourceStringList as Set

        expect:
        AdditionalCollections.asImmutableSet(sourceIntegerSet) == ImmutableSet.of(1, 2, 3)
        AdditionalCollections.asImmutableSet(sourceStringSet) == ImmutableSet.of("one", "two", "three")
        AdditionalCollections.asImmutableSet(sourceStringList) == ImmutableSet.of("one", "two", "three")
        AdditionalCollections.asImmutableSet([] as Set) == ImmutableSet.of()
    }

    def "asImmutableSet returns empty set when passed null"() {
        when:
        final ImmutableSet<String> actualResult = AdditionalCollections.asImmutableSet(null as Set<String>)

        then:
        actualResult == ImmutableSet.of()
    }

    def "asImmutableMap returns immutable copy of argument map"() {
        given:
        final Map<String, Integer> sourceMap = [one: 1, two: 2, three: 3]

        expect:
        AdditionalCollections.asImmutableMap(sourceMap) ==
            ImmutableMap.builder().put("one", 1).put("two", 2).put("three", 3).build()
        AdditionalCollections.asImmutableMap([:]) == ImmutableMap.of()
    }

    def "asImmutableMap returns empty map when passed null"() {
        when:
        final ImmutableMap<String, Integer> actualResult =
            AdditionalCollections.asImmutableMap(null as Map<String, Integer>)

        then:
        actualResult == ImmutableMap.of()
    }

    def "asImmutableMultimap returns immutable copy of argument map"() {
        given:
        final Multimap<String, Integer> sourceMap = LinkedListMultimap.create()
        sourceMap.put("one", 1)
        sourceMap.put("one", 11)
        sourceMap.put("two", 2)
        sourceMap.put("three", 3)

        expect:
        AdditionalCollections.asImmutableMultimap(sourceMap) ==
            ImmutableMultimap
                .<String, Integer> builder()
                .put("one", 1)
                .put("one", 11)
                .put("two", 2)
                .put("three", 3)
                .build()
        AdditionalCollections.asImmutableMultimap(LinkedListMultimap.create()) == ImmutableMultimap.of()
    }

    def "asImmutableMultimap returns empty map when passed null"() {
        when:
        final ImmutableMultimap<String, Integer> actualResult =
            AdditionalCollections.asImmutableMultimap(null as Multimap<String, Integer>)

        then:
        actualResult == ImmutableMultimap.of()
    }

    def "notEmpty(Map) returns argument when not null or empty"() {
        given:
        final Map<String, Integer> mutableMap = [one: 1, two: 2, three: 3]
        final ImmutableMap<String, Integer> immutableMap = ImmutableMap.copyOf(mutableMap)

        expect:
        AdditionalCollections.notEmpty(mutableMap, MessageCode.error("mutableMap")) == mutableMap
        AdditionalCollections.notEmpty(immutableMap, MessageCode.error("immutableMap")) == immutableMap
    }

    @Unroll
    def "notEmpty(Map) throws exception when argument is #state"() {
        when:
        AdditionalCollections.notEmpty(argument as Map<String, Integer>, MessageCode.error("argumentName"))

        then:
        final RequestValidationException e = thrown()
        e.getStatus() == HttpStatus.BAD_REQUEST
        e.getMessageCode() == MessageCode.error("argumentName")

        where:
        state   | argument
        "null"  | null as Map<String, Integer>
        "empty" | Collections.emptyMap()
    }

}
