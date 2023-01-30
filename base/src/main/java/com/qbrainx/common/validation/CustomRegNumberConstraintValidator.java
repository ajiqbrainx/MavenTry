package com.qbrainx.common.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CustomRegNumberConstraintValidator implements ConstraintValidator<CustomRegNumber, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null) {
            String regEx = "[A-Za-z]{2}[0-9]{1,2}[A-Za-z]{0,3}[0-9]{1,4}";
            Pattern pattern = Pattern.compile(regEx);
            Matcher matcher = pattern.matcher(value);
            return matcher.matches();
        }
        return false;
    }

}
