package com.qbrainx.common.lang.string;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * Utility methods for working with Strings.
 */
public final class Strings {

    public static Optional<String> absentIfBlank(final String candidate) {
        return Optional.ofNullable(StringUtils.trimToNull(candidate));
    }

    private Strings() {
    }
}
