package ar.com.ariel17.core.domain.transaction.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = TypeAndAccountsValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TypeAndAccounts {
    String message();
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}