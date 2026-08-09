package com.serwerigora.hotel.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PhoneNumberValidator
        implements ConstraintValidator<PhoneNumber, String> {

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?[0-9][0-9\\s().-]{6,20}[0-9]$"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        return PHONE_PATTERN.matcher(value).matches();
    }
}