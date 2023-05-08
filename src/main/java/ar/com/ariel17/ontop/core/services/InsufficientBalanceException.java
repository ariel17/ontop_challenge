package ar.com.ariel17.ontop.core.services;

/**
 * Exception thrown when the user does not have enough balance for the
 * transaction.
 */
public class InsufficientBalanceException extends TransactionException {

    public InsufficientBalanceException(String message) {
        super(message);
    }
}
