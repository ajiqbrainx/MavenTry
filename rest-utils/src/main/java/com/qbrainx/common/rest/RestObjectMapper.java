package com.qbrainx.common.rest;

import com.qbrainx.common.rest.CustomBaseModule;
import com.qbrainx.common.rest.RestObjectMapper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import io.vavr.jackson.datatype.VavrModule;

import java.util.TimeZone;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS;

/**
 * Provides a Jackson Object Mapper configured for services.
 */
public class RestObjectMapper {

    public static final ObjectMapper INSTANCE = createInstance(null);

    public static final ObjectMapper SMILE_INSTANCE = createInstance(new SmileFactory());

    public static final XmlMapper XML_INSTANCE = createXmlInstance();

    private static boolean classExists(final String name) {
        try {
            Class.forName(name, false, RestObjectMapper.class.getClassLoader());
            return true;
        } catch (final ClassNotFoundException e) {
            return false;
        }
    }

    private static XmlMapper createXmlInstance() {

        final XmlMapper mapper = new XmlMapper();

        initialize(mapper);
        mapper.registerModule(new JaxbAnnotationModule());

        return mapper;
    }

    private static ObjectMapper createInstance(final JsonFactory factory) {

        final ObjectMapper mapper = new ObjectMapper(factory);

        initialize(mapper);
        mapper.enable(ALLOW_COMMENTS); // allow Java-style comments in JSON
        mapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);

        return mapper;
    }

    private static void initialize(final ObjectMapper mapper) {
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new GuavaModule());
        mapper.registerModule(new VavrModule());
        mapper.registerModule(new CustomBaseModule());
        if (classExists("org.joda.time.DateTime") && classExists("com.fasterxml.jackson.datatype.joda.JodaModule")) {
            mapper.registerModule(new JodaModule());
        }

        mapper.setDateFormat(new StdDateFormat());
        mapper.setTimeZone(TimeZone.getDefault());

        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); // ignore unrecognized fields
        mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
    }

}
