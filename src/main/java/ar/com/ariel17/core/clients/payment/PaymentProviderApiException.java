package ar.com.ariel17.core.clients.payment;

public class PaymentProviderApiException extends Exception {

    public PaymentProviderApiException(String message) {
        super(message);
    }

    public PaymentProviderApiException(Throwable cause) {
        super(cause);
    }
}
