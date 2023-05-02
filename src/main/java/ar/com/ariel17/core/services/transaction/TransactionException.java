package ar.com.ariel17.core.services.transaction;

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
