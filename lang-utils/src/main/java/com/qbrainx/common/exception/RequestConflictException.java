package com.qbrainx.common.exception;

import org.springframework.http.HttpStatus;

import com.qbrainx.common.message.MessageCode;

/**
 * Exception thrown the request could not be completed due to a conflict with the current state of the target resource.
 */
public class RequestConflictException extends CustomException {

    private static final long serialVersionUID = 2497842896226264007L;

    public RequestConflictException(final MessageCode messageCode) {
        super(HttpStatus.CONFLICT, messageCode);
    }
}
