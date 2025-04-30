package com.project.mycars.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NameValidator implements ConstraintValidator<ValidName, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{fields.missing}")
                    .addConstraintViolation();
            return false;
        }

        if (!value.matches("^[A-Za-zÀ-ÖØ-öø-ÿ\\s]+$")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{fields.invalid}")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
