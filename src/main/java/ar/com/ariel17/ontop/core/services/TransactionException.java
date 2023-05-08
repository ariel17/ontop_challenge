package ar.com.ariel17.ontop.core.services;

/**
 * Errors related to internal operations on the service.
 */
public class TransactionException extends Exception {

    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(String message, Throwable reason) {
        super(message, reason);
    }

    public TransactionException(Throwable reason) {
        super(reason);
    }
}
