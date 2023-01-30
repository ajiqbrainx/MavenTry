package com.qbrainx.common.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CustomNameConstraintValidator implements ConstraintValidator<CustomName, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null) {
            String regEx = "^[a-zA-Z `s]+$";
            Pattern pattern = Pattern.compile(regEx);
            Matcher matcher = pattern.matcher(value);
            return matcher.matches();
        }
        return false;
    }

}
