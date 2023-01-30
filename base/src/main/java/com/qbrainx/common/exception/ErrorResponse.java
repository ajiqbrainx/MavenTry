package com.qbrainx.common.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qbrainx.common.message.MessageCode;

import lombok.Value;

import java.util.Collections;
import java.util.List;

@Value
public class ErrorResponse {

    List<MessageCode> errors;

    @JsonCreator
    private static ErrorResponse create(@JsonProperty("errors") final List<MessageCode> theErrors) {
        return errors(theErrors);
    }

    public static ErrorResponse errors(final List<MessageCode> theErrors) {
        return new ErrorResponse(theErrors);
    }

    public static ErrorResponse error(final MessageCode error) {
        return new ErrorResponse(Collections.singletonList(error));
    }
}
