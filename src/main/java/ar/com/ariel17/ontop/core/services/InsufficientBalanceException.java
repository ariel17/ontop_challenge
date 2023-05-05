package ar.com.ariel17.ontop.core.services;

public class InsufficientBalanceException extends TransactionException {

    public InsufficientBalanceException(String message) {
        super(message);
    }
}
