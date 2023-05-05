package ar.com.ariel17.ontop.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

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

    private MovementType type;

    private Operation operation;

    private Currency currency;

    private BigDecimal amount;

    private BankAccountOwner onTopAccount;

    private BankAccountOwner externalAccount;

    private Long walletTransactionId;

    private Payment payment;

    private Date createdAt;

    public void setWalletTransactionId(Long walletTransactionId) {
        this.walletTransactionId = walletTransactionId;
    }

    /**
     * Verifies if the movement has a complete state: transaction details and
     * payment operation ID.
     *
     * @return True if the object has payment ID and is valid.
     */
    public boolean isComplete() {
        return walletTransactionId != null && (type == MovementType.FEE || payment != null);
    }
}