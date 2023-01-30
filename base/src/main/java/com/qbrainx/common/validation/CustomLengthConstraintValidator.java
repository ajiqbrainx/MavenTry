package com.qbrainx.common.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CustomLengthConstraintValidator implements ConstraintValidator<CustomLength, CharSequence> {

    private int min;
    private int max;

    /**
     * Initializes the validator in preparation for
     * {@link #isValid(CharSequence, ConstraintValidatorContext)} calls.
     * The constraint annotation for a given constraint declaration
     * is passed.
     *
     * <p>This method is guaranteed to be called before any use of this instance for
     * validation.
     *
     * <p>The default implementation is a no-op.
     *
     * @param constraintAnnotation annotation instance for a given constraint declaration
     */
    @Override
    public void initialize(CustomLength constraintAnnotation) {
        min = constraintAnnotation.min();
        max = constraintAnnotation.max();
    }

    /**
     * Implements the validation logic.
     * The state of {@code value} must not be altered.
     *
     * <p>This method can be accessed concurrently, thread-safety must be ensured
     * by the implementation.
     *
     * @param value   object to validate
     * @param context context in which the constraint is evaluated
     * @return {@code false} if {@code value} does not pass the constraint
     */
    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        boolean isValid = (value == null); // If value is null, then it is not considered for length check and so it is valid.

        if (!isValid) {
            final int length = value.length();
            isValid = (length >= min && length <= max);
        }
        return isValid;
    }

}
