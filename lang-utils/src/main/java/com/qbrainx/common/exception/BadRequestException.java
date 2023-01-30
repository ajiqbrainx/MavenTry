package com.qbrainx.common.exception;

import org.springframework.http.HttpStatus;

import com.qbrainx.common.message.MessageCode;

public class BadRequestException extends CustomException {

    public BadRequestException(final MessageCode messageCode) {
        super(HttpStatus.BAD_REQUEST, messageCode);
    }

}
