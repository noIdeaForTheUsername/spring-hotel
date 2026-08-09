package com.serwerigora.hotel.shared.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumber {

    String message() default "Nieprawidłowy numer telefonu";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}