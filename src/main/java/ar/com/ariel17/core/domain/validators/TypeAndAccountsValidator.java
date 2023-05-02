package ar.com.ariel17.core.domain.validators;

import ar.com.ariel17.core.domain.BankAccount;
import ar.com.ariel17.core.domain.Movement;
import ar.com.ariel17.core.domain.Type;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TypeAndAccountsValidator implements ConstraintValidator<TypeAndAccounts, Movement> {

    public void initialize(TypeAndAccounts constraintAnnotation) {
    }

    /**
     * Validates if the type, from and to field values are valid for the
     * movement.
     *
     * @param object object to validate
     * @param context context in which the constraint is evaluated
     *
     * @return
     */
    public boolean isValid(Movement object, ConstraintValidatorContext context) {
        if (!(object instanceof Movement)) {
            throw new IllegalArgumentException("@TypeAndAccount only applies to Movement objects");
        }

        BankAccount from = object.getFrom();
        BankAccount to = object.getTo();

        if (object.getType() == Type.FEE) {
            return from == null && to == null;
        }
        return from != null && to != null && from != to;
    }
}