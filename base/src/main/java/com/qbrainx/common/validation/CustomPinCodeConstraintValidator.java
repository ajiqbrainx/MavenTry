package com.qbrainx.common.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CustomPinCodeConstraintValidator implements ConstraintValidator<CustomPincode, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null) {
            String regEx = "^[0-9]*$";
            Pattern pattern = Pattern.compile(regEx);
            Matcher matcher = pattern.matcher(value);
            return matcher.matches();
        }
        return false;
    }

}
