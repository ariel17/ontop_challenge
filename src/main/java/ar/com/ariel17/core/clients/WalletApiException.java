package ar.com.ariel17.core.clients;

public class WalletApiException extends Exception {

    public WalletApiException(String message) {
        super(message);
    }

    public WalletApiException(Throwable cause) {
        super(cause);
    }
}
