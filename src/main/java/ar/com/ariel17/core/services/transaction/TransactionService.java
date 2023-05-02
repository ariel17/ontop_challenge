package ar.com.ariel17.core.services.transaction;

import ar.com.ariel17.core.domain.bank.BankAccountOwner;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

/**
 * Defines the contract to manage transactions.
 */
public interface TransactionService {

    /**
     * Creates a transfer from the wallet to the indicated recipient.
     *
     * @param userId The user ID that creates the transfer.
     * @param recipient The bank account owner data to transfer money to.
     * @param amount The amount of money to egress.
     * @return A transaction with required movements.
     */
    void transfer(@NotNull Integer userId, @NotNull BankAccountOwner recipient, @NotNull BigDecimal amount) throws TransactionException;
}
