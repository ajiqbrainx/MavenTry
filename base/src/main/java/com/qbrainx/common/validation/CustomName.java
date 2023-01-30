package com.qbrainx.common.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

import com.qbrainx.common.message.MessageConstants;

@Constraint(validatedBy = CustomNameConstraintValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomName {

    String fieldCode();

    String fieldDefaultValue();

    String message() default MessageConstants.INVALIDNAMEFORMAT;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
