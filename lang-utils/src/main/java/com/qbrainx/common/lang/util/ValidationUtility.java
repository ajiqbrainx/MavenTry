package com.qbrainx.common.lang.util;

import com.qbrainx.common.exception.CustomException;
import com.qbrainx.common.message.MessageConstants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

@UtilityClass
public class ValidationUtility {

    public boolean validatePattern(String value, String regex) {
        if (value != null) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(value);
            boolean isMatch = matcher.matches();
            if (!isMatch) {
                throw new CustomException(HttpStatus.BAD_REQUEST, MessageConstants.INVALIDFORMAT + "  " + value);
            }
            return true;
        }
        return false;
    }

}
