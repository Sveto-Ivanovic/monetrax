package com.monetrax.monetrax.common.validaton.annotation;

import com.monetrax.monetrax.common.validaton.validator.DateCorrectnessValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DateCorrectnessValidator.class)
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DateCorrectnessConstraint {

    enum DateFilter {
        YEARS,
        MONTHS,
        DAYS
    }

    String message() default "Invalid Date!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    /**
     * The unit used to calculate the date difference.
     */
    DateFilter filter() default DateFilter.YEARS;
    /**
     * The age/date limit.
     * A negative value means the date must be at least this many units in the past. ex. -18 means that the date hast to be 18 or more Days/Months/Years older than today.
     * A positive value means the date must be at most this many units in the past. ex. 18 means that the date hast to between 18 years ago and now.
     */
    int value() default -18;
}
