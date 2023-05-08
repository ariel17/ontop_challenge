package ar.com.ariel17.ontop.core.clients;

/**
 * Wallet API exceptions for error scenarios not related to expected flows.
 */
public class WalletApiException extends Exception {

    public WalletApiException(String message) {
        super(message);
    }

    public WalletApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public WalletApiException(Throwable cause) {
        super(cause);
    }
}
