package ar.com.ariel17.ontop.core.domain;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Transaction represents a group of movements related to the same operation.
 */
@Data
public class Transaction {

    private BankAccountOwner externalAccount;

    private List<Movement> movements;

    private Payment payment;

    public Transaction() {
        movements = new ArrayList<>();
    }

    /**
     * @param movement The movement to add to the transaction.
     */
    public void addMovement(@NonNull Movement movement) {
        movements.add(movement);
    }

    /**
     * @return The sum of all movements amount.
     */
    public BigDecimal total() {
        return movements.stream().map(m -> m.getAmount()).
                reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Updates non-fee movements with associated payment ID.
     *
     * @param payment The payment operation response.
     */
    public void setPayment(Payment payment) {
        this.payment = payment;
        movements.stream().forEach(m -> m.setPayment(payment));
    }

    public void setWalletTransactionId(@NonNull Long walletTransactionId) {
        movements.stream().forEach(m -> m.setWalletTransactionId(walletTransactionId));
    }
}
