package ar.com.ariel17.core.repositories.movement;

public class MovementRepositoryException extends Exception {

    public MovementRepositoryException(String message) {
        super(message);
    }

    public MovementRepositoryException(Throwable cause) {
        super(cause);
    }
}
