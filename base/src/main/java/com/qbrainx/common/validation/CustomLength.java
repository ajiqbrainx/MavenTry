package com.qbrainx.common.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

import com.qbrainx.common.message.MessageConstants;

@Constraint(validatedBy = {CustomLengthConstraintValidator.class, CustomLengthNumberConstraintValidator.class})
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomLength {

    int min() default 0;

    int max() default Integer.MAX_VALUE;

    String fieldCode();

    String fieldDefaultValue();


    String message() default MessageConstants.LENGTH;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
