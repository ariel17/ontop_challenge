package ar.com.ariel17.core.domain.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the contract to validate a non-zero big decimal value.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {NonZeroBigDecimalValidator.class})
public @interface NonZeroBigDecimal {

    String message() default "Value must be positive or negative (excluding zero)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}