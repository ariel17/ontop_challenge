package ar.com.ariel17.ontop.core.services;

public class BankAccountException extends Exception {

    public BankAccountException(String message) {
        super(message);
    }

    public BankAccountException(Throwable reason) {
        super(reason);
    }
}
