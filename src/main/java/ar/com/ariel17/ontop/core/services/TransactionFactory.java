package ar.com.ariel17.ontop.core.services;

import ar.com.ariel17.ontop.core.domain.*;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;

/**
 * Factory pattern for Transaction with handy builder methods associated to
 * operation.
 */
@AllArgsConstructor
public class TransactionFactory {

    private BigDecimal feePercent;

    /**
     * Creates a withdrawal transaction, with fee.
     *
     * @param userId The user ID that created the transaction.
     * @param from   The bank account owner data from where take money to transfer.
     * @param to     The bank account owner data as target for withdrawal.
     * @param amount The amount of currency to withdraw.
     * @return The transaction with associated movements.
     */
    public Transaction createWithdraw(Long userId, BankAccountOwner onTopAccount, BankAccountOwner externalAccount, BigDecimal amount) throws TransactionException {
        Currency currency = onTopAccount.getBankAccount().getCurrency();

        if (!currency.equals(externalAccount.getBankAccount().getCurrency())) {
            throw new TransactionException("Cannot withdraw between different currencies");
        }

        Transaction transaction = new Transaction();
        Date now = new Date();

        transaction.addMovement(Movement.builder().
                userId(userId).
                type(MovementType.TRANSFER).
                operation(Operation.WITHDRAW).
                currency(currency).
                amount(amount).
                onTopAccount(onTopAccount).
                externalAccount(externalAccount).
                createdAt(now).
                build());

        transaction.addMovement(Movement.builder().
                userId(userId).
                type(MovementType.FEE).
                operation(Operation.WITHDRAW).
                currency(currency).
                amount(amount.multiply(feePercent)).
                createdAt(now).
                build());

        return transaction;
    }

    public void revertOperation(Transaction transaction, Operation operation, Long reverWalletTransactionId) {
        Date now = new Date();
        List<Movement> newMovements = new ArrayList<>();
        transaction.getMovements().stream().filter(m -> m.getOperation() == operation).forEach(m -> {
            newMovements.add(Movement.builder().
                    type(m.getType()).
                    operation(Operation.REVERT).
                    currency(m.getCurrency()).
                    amount(m.getAmount().negate()).
                    onTopAccount(m.getOnTopAccount()).
                    externalAccount(m.getExternalAccount()).
                    payment(m.getPayment()).
                    walletTransactionId(reverWalletTransactionId).
                    createdAt(now).
                    build());
        });
        List<Movement> movements = transaction.getMovements();
        movements.addAll(newMovements);
        transaction.setMovements(movements);
    }
}
