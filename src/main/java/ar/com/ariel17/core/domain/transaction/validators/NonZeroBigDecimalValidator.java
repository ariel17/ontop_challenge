package ar.com.ariel17.core.domain.transaction.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class NonZeroBigDecimalValidator implements ConstraintValidator<NonZeroBigDecimal, BigDecimal> {

    @Override
    public void initialize(NonZeroBigDecimal constraintAnnotation) {
    }

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        return value != null && (value.compareTo(BigDecimal.ZERO) > 0 || value.compareTo(BigDecimal.ZERO) < 0);
    }
}