package com.qbrainx.common.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

import com.qbrainx.common.message.MessageConstants;

@Constraint(validatedBy = CustomCompanyConstraintValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomCompany {
    
    String fieldCode();

    String fieldDefaultValue();

    String message() default MessageConstants.INVALIDCOMPANYNAMEFORMAT;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
