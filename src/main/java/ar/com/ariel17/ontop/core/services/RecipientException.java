package ar.com.ariel17.ontop.core.services;

public class RecipientException extends Exception {

    public RecipientException(String message) {
        super(message);
    }

    public RecipientException(Throwable reason) {
        super(reason);
    }
}
