package com.qbrainx.common.lang;

import com.qbrainx.common.exception.CustomException;
import com.qbrainx.common.exception.RequestValidationException;
import com.qbrainx.common.message.MessageCode;

import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

public final class Validations {

    public static <T> T notNull(final T objectToTest, final MessageCode messageCode) {
        return notNull(objectToTest, messageCode, RequestValidationException::new);
    }

    public static <T> T notNull(
        final T objectToTest,
        final MessageCode messageCode,
        final Function<MessageCode, ? extends CustomException> messageToException) {

        if (objectToTest == null) {
            throw messageToException.apply(messageCode);
        }

        return objectToTest;
    }

    public static String notBlank(final String stringToTest, final MessageCode messageCode) {
        return notBlank(stringToTest, messageCode, RequestValidationException::new);
    }

    public static String notBlank(
        final String stringToTest,
        final MessageCode messageCode,
        final Function<MessageCode, ? extends CustomException> messageToException) {

        if (StringUtils.isBlank(stringToTest)) {
            throw messageToException.apply(messageCode);
        }

        return stringToTest;
    }

    private Validations() {
        super();
    }

}
