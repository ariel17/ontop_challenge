package ar.com.ariel17.core.domain.transaction;

import lombok.NonNull;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Transaction represents a group of movements related to the same operation.
 */
@Validated
public class Transaction {

    private final List<Movement> movements;

    public Transaction() {
        movements = new ArrayList<>();
    }

    public List<Movement> getMovements() {
        return movements;
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
        BigDecimal total = new BigDecimal(0);
        for (Movement movement: movements) {
            total = total.add(movement.getAmount());
        }
        return total;
    }

    /**
     * Updates non-fee movements with associated payment ID.
     * @param paymentId The payment operation ID.
     */
    public void setPaymentId(@NonNull UUID paymentId) {
        for (Movement movement: movements) {
            if (movement.getType() != Type.FEE) {
                movement.setPaymentId(paymentId);
            }
        }
    }

    public void setWalletTransactionId(@NonNull Long walletTransactionId) {
        for (Movement movement: movements) {
            movement.setWalletTransactionId(walletTransactionId);
        }
    }
}
