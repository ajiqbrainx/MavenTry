package com.qbrainx.common.rest;

import com.qbrainx.common.rest.CustomBaseModule;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vavr.jackson.datatype.VavrModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
public class RestDatabindConfig {

    // Provides customized serialization/deserialization
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer objectMapperBuilder() {
        return jacksonObjectMapperBuilder ->
            jacksonObjectMapperBuilder
                .dateFormat(new StdDateFormat())
                .serializationInclusion(JsonInclude.Include.NON_ABSENT) // omit null and absent fields
                .featuresToEnable(JsonParser.Feature.ALLOW_COMMENTS) // allow Java-style comments
                .timeZone(TimeZone.getDefault())
                .featuresToDisable(
                    SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                    SerializationFeature.WRITE_DATES_WITH_ZONE_ID,
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, // ignore unrecognized fields
                    DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE) // preserve time zone
                .modules(
                    new Jdk8Module(),
                    new JavaTimeModule(),
                    new GuavaModule(),
                    new VavrModule(),
                    new CustomBaseModule())
                .annotationIntrospector(new JacksonAnnotationIntrospector() {
                    @Override
                    public JsonPOJOBuilder.Value findPOJOBuilderConfig(AnnotatedClass ac) {
                        if (ac.hasAnnotation(JsonPOJOBuilder.class)) {
                            return super.findPOJOBuilderConfig(ac);
                        }
                        return new JsonPOJOBuilder.Value("build", "");
                    }
                });

    }
}
