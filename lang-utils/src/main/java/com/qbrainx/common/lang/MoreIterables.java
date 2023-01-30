package com.qbrainx.common.lang;

import com.google.common.collect.Iterables;
import com.qbrainx.common.exception.RequestValidationException;
import com.qbrainx.common.message.MessageCode;

import java.util.function.Function;

/**
 * Iterable utility methods that are not provided by Guava {@code Iterables} and other Iterable utility classes.
 */
public final class MoreIterables {

    public static <T extends Iterable<?>> T notEmpty(final T iterable, final MessageCode code) {
        return notEmpty(iterable, code, RequestValidationException::new);
    }

    public static <T extends Iterable<?>> T notEmpty(
        final T iterable,
        final MessageCode code,
        final Function<MessageCode, ? extends RuntimeException> messageToException) {

//        if (iterable == null || Iterables.isEmpty(iterable)) {
//            throw messageToException.apply(code);
//        }
        if (iterable == null){
            throw messageToException.apply(code);
        }

        return iterable;
    }

    private MoreIterables() {
    }
}
