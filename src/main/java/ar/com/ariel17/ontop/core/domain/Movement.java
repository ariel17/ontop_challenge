package ar.com.ariel17.ontop.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.UUID;

/**
 * Movement represents a money displace. Relates the wallet user with a bank
 * account transaction.
 */
@AllArgsConstructor
@Builder
@Data
public class Movement {

    private Long id;

    private Long userId;

    private Type type;

    private Operation operation;

    private Currency currency;

    private BigDecimal amount;

    private BankAccount from;

    private BankAccount to;

    private Long walletTransactionId;

    private UUID paymentId;

    private Date createdAt;

    public void setWalletTransactionId(Long walletTransactionId) {
        this.walletTransactionId = walletTransactionId;
    }

    public void setPaymentId(UUID paymentId) {
        if (type == Type.FEE) {
            throw new IllegalArgumentException("Cannot set payment ID to a fee movement");
        }
        this.paymentId = paymentId;
    }

    /**
     * Verifies if the movement has a complete state: transaction details and
     * payment operation ID.
     *
     * @return True if the object has payment ID and is valid.
     */
    public boolean isComplete() {
        return walletTransactionId != null && (type == Type.FEE || paymentId != null);
    }
}