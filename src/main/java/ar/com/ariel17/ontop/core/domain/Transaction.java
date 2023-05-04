package ar.com.ariel17.ontop.core.domain;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Transaction represents a group of movements related to the same operation.
 */
@Getter
@Setter
public class Transaction {

    private List<Movement> movements;

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
     * @param paymentId The payment operation ID.
     */
    public void setPaymentId(@NonNull UUID paymentId) {
        movements.stream().filter(m -> m.getType() != Type.FEE).
                forEach(m -> m.setPaymentId(paymentId));
    }

    public void setWalletTransactionId(@NonNull Long walletTransactionId) {
        movements.stream().forEach(m -> m.setWalletTransactionId(walletTransactionId));
    }
}
