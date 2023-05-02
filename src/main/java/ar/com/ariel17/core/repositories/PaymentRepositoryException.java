package ar.com.ariel17.core.repositories;

public class PaymentRepositoryException extends Exception {

    public PaymentRepositoryException(String message) {
        super(message);
    }

    public PaymentRepositoryException(Throwable cause) {
        super(cause);
    }

}
