package com.qbrainx.common.exception;

import org.springframework.http.HttpStatus;

import com.qbrainx.common.message.MessageConstants;

/**
 * A general exception occurred during execution of a service for which there is not an applicable more specific
 * exception.
 */
@SuppressWarnings("unused")
public class ServiceExecutionException extends CustomException {

    private static final long serialVersionUID = -8080262418645909334L;

    public ServiceExecutionException(final Throwable e) {
        super(e, HttpStatus.INTERNAL_SERVER_ERROR, MessageConstants.TECHNICAL_PROBLEM);
    }

}
