package com.monetrax.monetrax.common.validaton.annotation;

import com.monetrax.monetrax.common.validaton.validator.EmailCorrectnessValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EmailCorrectnessValidator.class)
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailCorrectnessConstraint {
    String message() default "Invalid email format.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
