package com.qbrainx.common.message;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Builder(toBuilder = true)
@Getter
@EqualsAndHashCode
@JsonDeserialize(builder = MessageCode.MessageCodeBuilder.class)
@ToString
public class MessageCode implements Serializable {

    private static final long serialVersionUID = 6515223153693016635L;

    private final String code;
    private final MessageType type;
    private final String field;
    private final String defaultMessage;
    private final List<MessageParam> params;

    public static MessageCode error(final String theCode) {
        return msg(theCode, null, null);
    }

    public static MessageCode error(final String theCode,
                                    final MessageType messageType) {
        return msg(messageType, theCode, null, null);
    }

    public static MessageCode error(final String theCode,
                                    final String defaultMessage) {
        return msg(MessageType.ERROR, theCode, null, defaultMessage);
    }


    public static MessageCode error(final String theCode,
                                    final String defaultMessage,
                                    final MessageType messageType) {
        return msg(messageType, theCode, null, defaultMessage);
    }

    public static MessageCode error(final String theCode,
                                    final String theField,
                                    final String defaultMessage) {
        return msg(MessageType.ERROR, theCode, theField, defaultMessage);
    }

    public static MessageCode error(final MessageType messageType,
                                    final String theCode,
                                    final String theField,
                                    final String defaultMessage) {
        return msg(messageType, theCode, theField, defaultMessage);
    }

    public static MessageCode error(final String theCode,
                                    final String defaultMessage,
                                    final String paramCode,
                                    final String paramDefault) {
        return msg(theCode, defaultMessage, paramCode, paramDefault);
    }

    public static MessageCode error(final String theCode,
                                    final String theField,
                                    final String defaultMessage,
                                    final String paramCode,
                                    final String paramDefault) {
        return msg(MessageType.ERROR, theCode, theField, defaultMessage, paramCode, paramDefault);
    }

    @SuppressWarnings("OverloadedVarargsMethod")
    public static MessageCode error(final String theCode,
                                    final MessageParam... messageParams) {
        return msg(MessageType.ERROR, theCode, null, messageParams);
    }

    @SuppressWarnings("OverloadedVarargsMethod")
    public static MessageCode error(final String theCode,
                                    final String defaultMessage,
                                    final MessageParam... messageParams) {
        return msg(MessageType.ERROR, theCode, null, defaultMessage, messageParams);
    }

    public static MessageCode error(final String theCode,
                                    final String theField,
                                    final String defaultMessage,
                                    final MessageParam... messageParams) {
        return msg(MessageType.ERROR, theCode, theField, defaultMessage, messageParams);
    }

    public static MessageCode msg(final MessageType messageType,
                                  final String theCode,
                                  final String theField,
                                  final String paramCode,
                                  final String paramDefault) {
        return msg(messageType, theCode, theField, new MessageParam(paramCode, paramDefault));
    }

    public static MessageCode msg(final MessageType messageType,
                                  final String theCode,
                                  final String theField,
                                  final String defaultMessage,
                                  final String paramCode,
                                  final String paramDefault) {
        return msg(messageType, theCode, theField, defaultMessage, new MessageParam(paramCode, paramDefault));
    }

    public static MessageCode msg(final String theCode,
                                  final String paramCode,
                                  final String paramDefault) {
        return msg(MessageType.ERROR, theCode, null, new MessageParam(paramCode, paramDefault));
    }

    public static MessageCode msg(final String theCode,
                                  final String defaultMessage,
                                  final String paramCode,
                                  final String paramDefault) {
        return msg(MessageType.ERROR, theCode, null, defaultMessage, new MessageParam(paramCode, paramDefault));
    }

    public static MessageCode msg(final MessageType messageType,
                                  final String theCode,
                                  final String theField,
                                  final MessageParam... messageParams) {
        return MessageCode.builder()
                .code(theCode)
                .type(messageType)
                .field(theField)
                .params(Arrays.stream(messageParams).collect(Collectors.toList()))
                .build();
    }

    public static MessageCode msg(final MessageType messageType,
                                  final String theCode,
                                  final String theField,
                                  final String defaultMessage,
                                  final MessageParam... messageParams) {
        return MessageCode.builder()
            .code(theCode)
            .type(messageType)
            .field(theField)
            .defaultMessage(defaultMessage)
            .params(Arrays.stream(messageParams).collect(Collectors.toList()))
            .build();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class MessageCodeBuilder {
    }
}
