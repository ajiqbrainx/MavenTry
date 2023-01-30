package com.qbrainx.common.util;

import com.qbrainx.common.message.MessageCode;
import lombok.SneakyThrows;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * use {@link com.qbrainx.common.lang.Optionals}
 *
 * @see com.qbrainx.common.lang.Validations
 *
 */
@Component
@Deprecated
public class Validate<T> {
    /**
     * Checks whether the give data is present, if not throws the Not Found Exception.
     *
     * @param optionalData :optional data to be checked
     * @return : if Optional data is present will return the data.
     */
    public T checkPresent(final Optional<T> optionalData) {
        return checkPresent(optionalData, "PK_ID", "data not found");
    }

    /**
     * Checks whether the give data is present, if not throws the Not Found Exception.
     *
     * @param optionalData : optional data to be checked
     * @param verbose      : Error Data to throw with the value
     * @return : if Optional data is present will return the data.
     */
    public T checkPresent(final Optional<T> optionalData, final String verbose) {
        return checkPresent(optionalData, "PK_ID", verbose);
    }

    /**
     * Checks whether the give data is present, if not throws the Not Found Exception.
     *
     * @param optionalData : optional data to be checked
     * @param key          : key for the error data
     * @param verbose      : Error Data to throw with the value
     * @return : if Optional data is present will return the data.
     */
    @SneakyThrows
    public T checkPresent(final Optional<T> optionalData, final String key, final String verbose) {
        if (!optionalData.isPresent()) {
            throw new ChangeSetPersister.NotFoundException();
        }
        return optionalData.get();
    }

}
