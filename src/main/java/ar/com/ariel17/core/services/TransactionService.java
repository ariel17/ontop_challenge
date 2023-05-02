package ar.com.ariel17.core.services;

import ar.com.ariel17.core.domain.BankAccountOwner;
import ar.com.ariel17.core.domain.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Defines the contract to manage transactions.
 */
@Service
public interface TransactionService {

    /**
     * Creates a transfer from the wallet to the indicated recipient.
     *
     * @param userId The user ID that creates the transfer.
     * @param recipient The bank account owner data to transfer money to.
     * @param amount The amount of money to egress.
     */
    Transaction transfer(Long userId, BankAccountOwner recipient, BigDecimal amount) throws TransactionException;
}
