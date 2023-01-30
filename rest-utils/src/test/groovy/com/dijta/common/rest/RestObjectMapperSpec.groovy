package com.dijta.common.rest

import com.google.common.collect.ImmutableList
import groovy.transform.EqualsAndHashCode
import spock.lang.Specification
import spock.lang.Unroll

import javax.xml.bind.annotation.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 *
 */
@Unroll
class RestObjectMapperSpec extends Specification {

    def 'serialize/deserialize #yearMonth to/from #format'() {
        expect:
        format == RestObjectMapper.INSTANCE.writer().writeValueAsString(yearMonth)
        yearMonth == RestObjectMapper.INSTANCE.readerFor(YearMonth).readValue(format)

        where:
        yearMonth              || format
        YearMonth.of(2017, 3)  || '"2017-03"'
        YearMonth.of(678, 12)  || '"0678-12"'
        YearMonth.of(9999, 12) || '"9999-12"'
    }

    def "serialize/deserialize #date to/from ISO_DATE #format"() {
        expect:
        format == RestObjectMapper.INSTANCE.writer().writeValueAsString(date)
        date == RestObjectMapper.INSTANCE.readerFor(LocalDate.class).readValue(format)

        where:
        date                       || format
        LocalDate.of(1990, 3, 12)  || '"1990-03-12"'
        LocalDate.of(990, 3, 12)   || '"0990-03-12"'
        LocalDate.of(-500, 4, 12)  || '"-0500-04-12"'
        LocalDate.of(10000, 4, 12) || '"+10000-04-12"'
    }

    def "serialize/deserialize #uuid to/from UUID #format"() {
        expect:
        format == RestObjectMapper.INSTANCE.writer().writeValueAsString(uuid)
        uuid == RestObjectMapper.INSTANCE.readerFor(UUID.class).readValue(format)

        where:
        uuid             || format
        new UUID(4, 5)   || '"00000000-0000-0004-0000-000000000005"'
        new UUID(18, 11) || '"00000000-0000-0012-0000-00000000000b"'
    }

    def "serialize/deserialize ZonedDateTime #datetime to/from ISO_ZONED_DATE_TIME #format"() {
        expect:
        format == RestObjectMapper.INSTANCE.writer().writeValueAsString(datetime)
        datetime == RestObjectMapper.INSTANCE.readerFor(ZonedDateTime.class).readValue(format)

        where:
        datetime                                                          || format
        ZonedDateTime.of(2007, 12, 3, 10, 15, 30, 0, ZoneId.of('+01:00')) || '"2007-12-03T10:15:30+01:00"'
    }

    def 'smile serialization'() {
        setup:
        def originalBucket = new Bucket(
            locale: Locale.CANADA,
            name: 'foo',
            uuid: UUID.randomUUID())

        def standardBytes = RestObjectMapper.INSTANCE.writeValueAsBytes(originalBucket)
        def smileBytes = RestObjectMapper.SMILE_INSTANCE.writeValueAsBytes(originalBucket)

        def standardBucket = RestObjectMapper.INSTANCE.readValue(standardBytes, Bucket)
        def smileBucket = RestObjectMapper.SMILE_INSTANCE.readValue(smileBytes, Bucket)

        expect:
        standardBytes.length > smileBytes.length
        standardBucket == smileBucket
        smileBucket == originalBucket
    }

    def 'XML serialization'() {
        given:
        def original = new Bucket(
            locale: Locale.CANADA,
            name: 'foo',
            uuid: UUID.randomUUID(),
            lines: ImmutableList.of('foo', 'bar'))

        when:
        def str = RestObjectMapper.XML_INSTANCE.writeValueAsString(original)
        def reconstructed = RestObjectMapper.XML_INSTANCE.readerFor(Bucket).readValue(str)

        then:
        str.contains('<message>')
        str.contains('<messageLines>')
        str.contains('<messageLine>foo')
        reconstructed == original
    }

    def "deserialize with mapped parent ref annotation sets parents" () {

    }

    @XmlRootElement(name = "message")
    @XmlAccessorType(XmlAccessType.FIELD)
    @EqualsAndHashCode
    static class Bucket {
        @XmlElement
        Locale locale
        @XmlElement
        String name
        @XmlElement
        UUID uuid
        @XmlElementWrapper(name = "messageLines", required = true)
        @XmlElement(name = "messageLine", required = true)
        ImmutableList<String> lines
    }


    static class Parent {
        private String parentId;
        @MappedParentRef(mappedBy = "parent")
        private List<ChildOne> childOneList;
        @MappedParentRef(mappedBy = "parent")
        private List<ChildTwo> childTwoList;
    }

    static class ChildOne {
        private String childOneId;
        private Parent parent;
        @MappedParentRef(mappedBy = "childTwo")
        private GrantChild grantChild;
    }

    static class ChildTwo {
        private String childTwoId;
        private Parent parent;
    }

    static class GrantChild {
        private String grantChildId;
        private ChildTwo childTwo;
    }

}
