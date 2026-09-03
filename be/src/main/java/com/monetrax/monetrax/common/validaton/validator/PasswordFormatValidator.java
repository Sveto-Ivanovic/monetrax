package com.monetrax.monetrax.common.validaton.validator;

import com.monetrax.monetrax.common.validaton.annotation.PasswordFormatConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PasswordFormatValidator implements ConstraintValidator<PasswordFormatConstraint, String> {
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext ctx){
        return value != null && PASSWORD_PATTERN.matcher(value).matches();
    }

}
