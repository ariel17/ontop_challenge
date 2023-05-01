package ar.com.ariel17.core.domain;

/**
 * BankAccount represents the minimal data required to identify a bank account.
 */
public record BankAccount(Integer routing, Integer account) {

    /**
     * Creates a new bank account object.
     *
     * @param routing The bank's routing number.
     * @param account The account number inside bank.
     */
    public BankAccount(final Integer routing, final Integer account) {
        this.routing = routing;
        this.account = account;
    }
}