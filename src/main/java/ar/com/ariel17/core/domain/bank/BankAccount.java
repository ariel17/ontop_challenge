package ar.com.ariel17.core.domain.bank;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;

import java.util.Currency;

/**
 * BankAccount represents the minimal data required to identify a bank account.
 */
@Validated
public class BankAccount {

    @Positive(message = "Routing number has to be positive")
    private final Integer routing;

    @Positive(message = "Account number has to be positive")
    private final Integer account;

    @NotNull(message = "Currency cannot be null")
    private final Currency currency;


    /**
     * Creates a new bank account object.
     *
     * @param routing  The bank's routing number.
     * @param account  The account number inside bank.
     * @param currency The bank account's currency.
     */
    public BankAccount(Integer routing, final Integer account, final Currency currency) {
        this.routing = routing;
        this.account = account;
        this.currency = currency;
    }

    public Integer getRouting() {
        return routing;
    }

    public Integer getAccount() {
        return account;
    }

    public Currency getCurrency() {
        return currency;
    }
}