package com.dijta.common.lang.string

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import spock.lang.Specification
import com.dijta.common.converter.LocalDateConverter;
import spock.lang.Unroll

import java.time.LocalDate

/**
 * Unit test specification for the {@link Strings} class.
 *
 */
class LocalDateConverterSpec extends Specification {

	def "date converter"() {
		ObjectMapper mapper = new ObjectMapper();
		expect:
		mapper.readValue(input, DateClass.class).date == expected
		where:
		input                        | expected
		"{\"date\":\"1982-10-12\"}" | LocalDate.of(1982,10,12)
		"{}"                         | null
		"{\"date\":null}"            | null
		"{\"date\":\"0000-00-00\"}"  | null
	}
}

class DateClass {
	@JsonDeserialize(converter = LocalDateConverter.class)
	public LocalDate date;
}


