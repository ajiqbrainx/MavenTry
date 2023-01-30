package com.qbrainx.common.exception;

import com.qbrainx.common.message.MessageCode;
import com.qbrainx.common.message.MessageConstants;
import com.qbrainx.common.validation.CustomValidations;

import javax.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

@Log4j2
@RestControllerAdvice
public class CustomRestExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomBusinessException(final CustomException exception) {
        log.error("Business error: {}", exception.getMessageCode());
        return buildErrorResponse(exception.getStatus(), Collections.singletonList(exception.getMessageCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleValidationException(final MethodArgumentNotValidException exception) {
        final List<MessageCode> errors = CustomValidations.getValidationErrors(exception);
        log.error("Validation error: {}", errors);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ErrorResponse> handleConstraintViolationException(final ConstraintViolationException exception) {
        final List<MessageCode> errors = CustomValidations.getValidationErrors(exception);
        log.error("Validation error of ConstraintViolationException: {}", errors);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorResponse> handleAccessDeniedException(final AccessDeniedException exception) {
        log.error("Access exception: ", exception);
        return buildErrorResponse(HttpStatus.FORBIDDEN, MessageConstants.FORBIDDEN);
    }

    @ExceptionHandler(ResponseStatusException.class)
    protected ResponseEntity<ErrorResponse> handleResponseStatusException(final ResponseStatusException exception) {
        log.error("ResponseStatusException : ", exception);
        return buildTechnicalError(exception.getStatus(), exception.getReason());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleGenericException(final Exception exception) {
        log.error("Runtime exception: ", exception);
        return buildTechnicalError(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
        final HttpStatus httpStatus,
        final String messageCode) {

        return ResponseEntity
            .status(httpStatus)
            .body(ErrorResponse.error(MessageCode.error(messageCode)));
    }

    private ResponseEntity<ErrorResponse> buildTechnicalError(
        final HttpStatus httpStatus,
        final String message) {

        return ResponseEntity
            .status(httpStatus)
            .body(ErrorResponse.error(MessageCode.error(MessageConstants.TECHNICAL_PROBLEM, null, null, "NONE", message)));
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
        final HttpStatus httpStatus,
        final List<MessageCode> errors) {

        return ResponseEntity
            .status(httpStatus)
            .body(ErrorResponse.errors(errors));
    }

}
