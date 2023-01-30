package com.qbrainx.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import com.qbrainx.common.message.MessageCode;
import com.qbrainx.common.message.MessageParam;

@Getter
public class CustomException extends RuntimeException {

    private static final long serialVersionUID = 7962785205942445939L;
    private final HttpStatus status;
    private final MessageCode messageCode;

    public CustomException(final Throwable e,
                          final HttpStatus status,
                          final MessageCode messageCode) {

        super(e);
        this.status = status;
        this.messageCode = messageCode;
    }

    public CustomException(final HttpStatus status,
                          final MessageCode messageCode) {
        super(messageCode.getCode());
        this.status = status;
        this.messageCode = messageCode;
    }

    public CustomException(final Throwable e,
                          final HttpStatus status,
                          final String messageCode) {
        this(e, status, MessageCode.error(messageCode));
    }

    public CustomException(final Throwable e,
                          final HttpStatus status,
                          final String messageCode,
                          final String defaultMessage) {
        this(e, status, MessageCode.error(messageCode, defaultMessage));
    }

    @Deprecated
    public CustomException(final HttpStatus status,
                          final String messageCode) {
        this(status, MessageCode.error(messageCode));
    }

    public CustomException(final HttpStatus status,
                          final String messageCode,
                          final String defaultMessage) {
        this(status, MessageCode.error(messageCode, defaultMessage));
    }

    public CustomException(final HttpStatus status,
                          final String messageCode,
                          final MessageParam... params) {
        this(status, MessageCode.error(messageCode, params));
    }

    public CustomException(final HttpStatus status,
                          final String messageCode,
                          final String defaultMessage,
                          final MessageParam... params) {
        this(status, MessageCode.error(messageCode, defaultMessage, params));
    }

    public CustomException(final HttpStatus status,
                          final String messageCode,
                          final String field,
                          final String param,
                          final String paramDefaultVal) {
        this(status, MessageCode.error(messageCode, field, param, paramDefaultVal));
    }

    public CustomException(final HttpStatus status,
                          final String messageCode,
                          final String field,
                          final String defaultMessage,
                          final String param,
                          final String paramDefaultVal) {
        this(status, MessageCode.error(messageCode, field, defaultMessage, param, paramDefaultVal));
    }
}
