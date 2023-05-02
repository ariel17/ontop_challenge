package ar.com.ariel17.core.domain.transaction;

import ar.com.ariel17.core.domain.bank.BankAccount;
import ar.com.ariel17.core.domain.transaction.validators.NonZeroBigDecimal;
import ar.com.ariel17.core.domain.transaction.validators.TypeAndAccounts;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.UUID;

/**
 * Movement represents a money displace. Relates the wallet user with a bank
 * account transaction.
 */
@Validated
@TypeAndAccounts(message = "Invalid combination for `type`, `from` and `to fields.")
@AllArgsConstructor
@Data
public class Movement {

    private Long id;

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotNull(message = "Type cannot be null")
    private Type type;

    @NotNull(message = "Operation cannot be null")
    private Operation operation;

    @NotNull(message = "Currency cannot be null")
    private Currency currency;

    @NonZeroBigDecimal(message = "Amount cannot be zero")
    private BigDecimal amount;

    private BankAccount from;

    private BankAccount to;

    private Long walletTransactionId;

    private UUID paymentId;

    private Date createdAt;

    public void setWalletTransactionId(@NonNull Long walletTransactionId) {
        this.walletTransactionId = walletTransactionId;
    }

    public void setPaymentId(@NonNull UUID paymentId) {
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