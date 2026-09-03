package com.monetrax.monetrax.common.validaton.validator;

import com.monetrax.monetrax.common.validaton.annotation.EmailCorrectnessConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class EmailCorrectnessValidator implements ConstraintValidator<EmailCorrectnessConstraint, String> {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext ctx){
        return value == null || EMAIL_PATTERN.matcher(value).matches();
    }
}
