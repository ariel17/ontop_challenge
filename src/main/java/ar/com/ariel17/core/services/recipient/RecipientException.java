package ar.com.ariel17.core.services.recipient;

public class RecipientException extends Exception {

    public RecipientException(String message) {
        super(message);
    }

    public RecipientException(Throwable reason) {
        super(reason);
    }
}
