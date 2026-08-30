package com.monetrax.monetrax.common.validaton.annotation;

import com.monetrax.monetrax.common.validaton.validator.PasswordFormatValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = PasswordFormatValidator.class)
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordFormatConstraint {
    String message() default "Invalid Password format, password must contain at least 1 uppercase, lowercase, number and symbol.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
