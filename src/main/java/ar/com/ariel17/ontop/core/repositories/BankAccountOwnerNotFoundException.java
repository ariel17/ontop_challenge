package ar.com.ariel17.ontop.core.repositories;

/**
 * Thrown when the bank account owner is not found.
 */
public class BankAccountOwnerNotFoundException extends Exception {

    public BankAccountOwnerNotFoundException(String message) {
        super(message);
    }
}
