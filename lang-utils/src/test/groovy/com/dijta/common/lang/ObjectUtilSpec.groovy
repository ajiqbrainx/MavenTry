package com.dijta.common.lang

import com.dijta.common.lang.util.ObjectUtil
import spock.lang.Specification

class ObjectUtilSpec extends Specification{

    def "Validate the List"() {
        given:
        List<Integer> intValues = new ArrayList<>();
        intValues.add(1);

        List<String> stringList = new ArrayList<>();
        stringList.add("test")

        List<Long> longs = new ArrayList<>();
        longs.add(2L)
        expect:
        ObjectUtil.findFirst(intValues) == 1
        ObjectUtil.findFirst(stringList) == "test"
        ObjectUtil.findFirst(longs) == 2L
    }


    def "Validate the Empty List"() {
        when:
        List<String> strings = new ArrayList<>();
        ObjectUtil.findFirst(strings)
        then:
        thrown RuntimeException
    }

    def "Validate the Empty List with Proper Error Message"() {
        when:
        List<String> strings = new ArrayList<>();
        ObjectUtil.findFirst(strings)
        then:
        RuntimeException exception = thrown()
        exception.getLocalizedMessage() == 'Values present in the list are either empty or greater than 1'
    }

    def "List with null assigned"() {
        when:
        List<String> strings = null;
        ObjectUtil.findFirst(strings)
        then:
        RuntimeException exception = thrown()
        exception.getLocalizedMessage() == 'Values present in the list are either empty or greater than 1'
    }

}
