package ar.com.ariel17.ontop.core.clients;

/**
 * Payment provider exception for error scenarios.
 */
public class PaymentProviderApiException extends Exception {

    public PaymentProviderApiException(String message) {
        super(message);
    }

    public PaymentProviderApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public PaymentProviderApiException(Throwable cause) {
        super(cause);
    }
}
