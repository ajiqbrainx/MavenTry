package com.qbrainx.common.validation;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.qbrainx.common.message.MessageCode;
import com.qbrainx.common.message.MessageConstants;

@Log4j2
@UtilityClass
public class CustomValidations {

    public List<MessageCode> getValidationErrors(final MethodArgumentNotValidException exception) {
        return exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .sorted(new Comparator<FieldError>() {
                @Override
                public int compare(FieldError fe1, FieldError fe2) {
                    return fe1.getField().concat(fe1.getDefaultMessage()).compareTo(fe2.getField().concat(fe2.getDefaultMessage()));
                }
            })
            .map(fieldError -> {
                final Object[] arguments = fieldError.getArguments();
                if (arguments != null) {
                    MessageCode messageCode;
                    Optional<MessageCode> messageCodeOptional =  Arrays.stream(arguments)
                        .filter(it -> it instanceof MessageCode)
                        .map(MessageCode.class::cast)
                        .findAny();
                    if (messageCodeOptional.isPresent()) {
                        messageCode = messageCodeOptional.get();
                    } else {
                        if (arguments.length > 2) {
                            messageCode = MessageCode.error(fieldError.getDefaultMessage(), fieldError.getField(), fieldError.getCode(), String.valueOf(arguments[1]), String.valueOf(arguments[2]));
                        } else {
                            messageCode = MessageCode.error(MessageConstants.TECHNICAL_PROBLEM);
                        }
                    }
                    return messageCode;
                }
                return MessageCode.error(MessageConstants.TECHNICAL_PROBLEM);
            })
            .collect(Collectors.toList());
    }

    public List<MessageCode> getValidationErrors(final ConstraintViolationException exception) {
        log.info("exception:" + exception.getMessage());
        return exception.getConstraintViolations()
                .stream()
                .sorted(new Comparator<ConstraintViolation<?>>() {
                    @Override
                    public int compare(ConstraintViolation<?> cv1, ConstraintViolation<?> cv2) {
                        return String.valueOf(cv1.getPropertyPath()).concat(cv1.getMessage()).compareTo(String.valueOf(cv2.getPropertyPath()).concat(cv2.getMessage()));
                    }
                })
                .map(constraintViolation -> {
                    final Map<String, Object> map = constraintViolation.getConstraintDescriptor().getAttributes();
                    if (map != null) {
                        return MessageCode.error(String.valueOf(map.get("message")), //constraintViolation.getMessage(),
                        constraintViolation.getPropertyPath().toString().substring(constraintViolation.getPropertyPath().toString().indexOf(".") + 1),
                        constraintViolation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                        String.valueOf(map.get("fieldCode")),
                        String.valueOf(map.get("fieldDefaultValue"))
                        );
                    }
                    return MessageCode.error(MessageConstants.TECHNICAL_PROBLEM);
                })
                .collect(Collectors.toList());
    }

}
