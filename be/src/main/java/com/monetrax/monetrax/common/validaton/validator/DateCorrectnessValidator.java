package com.monetrax.monetrax.common.validaton.validator;

import com.monetrax.monetrax.common.validaton.annotation.DateCorrectnessConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.ChronoUnit;

public class DateCorrectnessValidator implements ConstraintValidator<DateCorrectnessConstraint, LocalDate> {

    private int value;
    private DateCorrectnessConstraint.DateFilter filter;

    public void initialize(DateCorrectnessConstraint constraintAnnotation) {
        this.value = constraintAnnotation.value();
        this.filter = constraintAnnotation.filter();
    }

    private boolean calcValidity(long val, int limit){
        if (limit < 0) {
            return val >= -limit;
        }

        return val <= limit;
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext ctx) {
        LocalDate localDateNow = LocalDate.now();
        long diff;
        return switch(filter){
            case YEARS -> {
                diff = ChronoUnit.YEARS.between(localDate, localDateNow);
                yield  calcValidity(diff, value);
            }
            case DAYS -> {
                diff = ChronoUnit.DAYS.between(localDate, localDateNow);
                yield  calcValidity(diff, value);
            }
            case MONTHS -> {
                diff = ChronoUnit.MONTHS.between(localDate, localDateNow);
                yield  calcValidity(diff, value);
            }

        };
    }
}