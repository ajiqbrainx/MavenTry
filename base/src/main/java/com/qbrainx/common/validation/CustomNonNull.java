package com.qbrainx.common.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

import com.qbrainx.common.message.MessageConstants;

@Constraint(validatedBy = CustomNotNullConstraintValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomNonNull {

    String fieldCode();

    String fieldDefaultValue();

    String message() default MessageConstants.NON_NULL;//"must be Not Null";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
