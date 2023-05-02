package ar.com.ariel17.core.services.transaction;

import ar.com.ariel17.core.domain.bank.BankAccountOwner;
import ar.com.ariel17.core.domain.transaction.Movement;
import ar.com.ariel17.core.domain.transaction.Operation;
import ar.com.ariel17.core.domain.transaction.Transaction;
import ar.com.ariel17.core.domain.transaction.Type;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

/**
 * Factory pattern for Transaction with handy builder methods associated to
 * operation.
 */
public class TransactionFactory {

    private BigDecimal feePercent;

    /**
     * Creates a new factory instance.
     *
     * @param feePercent The amount of fee to apply on the operation, if required.
     */
    public TransactionFactory(@NotNull final BigDecimal feePercent) {
        this.feePercent = feePercent;
    }

    /**
     * Creates an egress transaction, with fee.
     *
     * @param userId The user ID that created the transaction.
     * @param from The bank account owner data from where take money to transfer.
     * @param to The bank account owner data as target for the egress.
     * @param amount The amount of currency to egress.
     * @return The transaction with associated movements.
     */
    public Transaction createEgress(@NotNull final Integer userId, @NotNull final BankAccountOwner from, @NotNull final BankAccountOwner to, @NotNull final BigDecimal amount) throws TransactionException {
        Currency currency = from.getBankAccount().getCurrency();

        if (!currency.equals(to.getBankAccount().getCurrency())) {
            throw new TransactionException("Cannot egress money between different currencies");
        }

        Transaction transaction = new Transaction();
        Date now = new Date();

        Movement m = new Movement(null, userId, Type.TRANSFER, Operation.EGRESS, currency, amount, from.getBankAccount(), to.getBankAccount(), null, null, now);
        transaction.addMovement(m);

        BigDecimal fee = amount.multiply(feePercent);
        m = new Movement(null, userId, Type.FEE, Operation.EGRESS, currency, fee, null, null, null, null, now);
        transaction.addMovement(m);

        return transaction;
    }
}
