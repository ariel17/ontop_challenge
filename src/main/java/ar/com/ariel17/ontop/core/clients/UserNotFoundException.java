package ar.com.ariel17.ontop.core.clients;

/**
 * Thrown when Wallet API client does not found the user.
 */
public class UserNotFoundException extends Exception {

    public UserNotFoundException(String message) {
        super(message);
    }
}
