package com.qbrainx.common.exception;

import org.springframework.http.HttpStatus;

import com.qbrainx.common.message.MessageCode;

/**
 * Exception thrown when the server cannot or will not process the request due to something that is perceived to be a
 * client error (e.g., malformed request syntax, invalid request message framing, or deceptive request routing).
 */
public class RequestValidationException extends CustomException {

    private static final long serialVersionUID = 5973977852289369190L;

    public RequestValidationException(final MessageCode messageCode) {
        super(HttpStatus.BAD_REQUEST, messageCode);
    }

}
