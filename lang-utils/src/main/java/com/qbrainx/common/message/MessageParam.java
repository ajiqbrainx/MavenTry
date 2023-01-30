package com.qbrainx.common.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Value;
import lombok.ToString;

import java.io.Serializable;

@Value
@ToString
public class MessageParam implements Serializable {

    private static final long serialVersionUID = -5011941073620343846L;
    
    String code;
    String defaultValue;

    @JsonCreator
    private static MessageParam create(
        @JsonProperty("code") final String theCode,
        @JsonProperty("defaultValue") final String theDefaultValue) {

        return new MessageParam(theCode, theDefaultValue);
    }

    public static MessageParam[] messageParamList(final Map<String, String> messageParamMap) {
        return messageParamMap.entrySet()
                .stream()
                .map(entry -> new MessageParam(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList())
                .toArray(new MessageParam[0]);
    }

    public static MessageParam[] messageCodeList(final List<String> codes) {
        return codes.stream()
                .map(code -> new MessageParam(code, null))
                .collect(Collectors.toList())
                .toArray(new MessageParam[0]);
    }

    public static MessageParam[] messageParamList(final String... defaultValues) {
        return Arrays.stream(defaultValues)
                .map(defaultValue -> new MessageParam(null, defaultValue))
                .collect(Collectors.toList()).toArray(new MessageParam[0]);
    }
}
