package ar.com.ariel17.core.domain.transaction;

import ar.com.ariel17.core.domain.BaseModel;
import ar.com.ariel17.core.domain.Validable;
import ar.com.ariel17.core.domain.bank.BankAccount;
import ar.com.ariel17.core.domain.validators.NonZeroBigDecimal;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.UUID;

/**
 * Movement represents a money displace. Relates the wallet user with a bank
 * account transaction.
 */
public class Movement extends BaseModel<Integer> implements Validable {

    private Integer userId;

    @NotNull(message="Type cannot be null")
    private Type type;

    @NotNull(message="Operation cannot be null")
    private Operation operation;

    @NotNull(message="Currency cannot be null")
    private Currency currency;

    @NonZeroBigDecimal(message="Amount cannot be zero")
    private BigDecimal amount;

    private BankAccount from;

    private BankAccount to;

    private Integer walletTransactionId;

    private UUID paymentId;

    /**
     * Creates a new movement.
     *
     * @param id The unique id for this movement. If null, the movement was not yet stored.
     * @param userId
     * @param type
     * @param operation
     * @param currency
     * @param amount
     * @param from
     * @param to
     * @param walletTransactionId
     * @param paymentId
     * @param createdAt The movement creation date. If null, the movement was not yet stored.
     */
    public Movement(Integer id, Integer userId, Type type, Operation operation, Currency currency, BigDecimal amount, BankAccount from, BankAccount to, Integer walletTransactionId, UUID paymentId, Date createdAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.operation = operation;
        this.currency = currency;
        this.amount = amount;
        this.from = from;
        this.to = to;
        this.paymentId = paymentId;
        this.walletTransactionId = walletTransactionId;
        this.createdAt = createdAt;
    }

    public Integer getUserId() {
        return userId;
    }

    public Type getType() {
        return type;
    }

    public Operation getOperation() {
        return operation;
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BankAccount getFrom() {
        return from;
    }

    public BankAccount getTo() {
        return to;
    }

    public void setWalletTransactionId(@NotNull final Integer walletTransactionId) {
        this.walletTransactionId = walletTransactionId;
    }

    public Integer getWalletTransactionId() {
        return this.walletTransactionId;
    }

    public void setPaymentId(@NotNull final UUID paymentId) {
        this.paymentId = paymentId;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    @Override
    public boolean isValid() {
        if (type == Type.FEE) {
            return from == null && to == null;
        }
        return from != null && to != null && from != to;
    }

    /**
     * Verifies if the movement has a complete state: transaction details and
     * payment operation ID.
     *
     * @return True if the object has payment ID and is valid.
     */
    public boolean isComplete() {
        if (walletTransactionId == null) {
            return false;
        }
        if (type != Type.FEE && paymentId == null) {
            return false;
        }
        return isValid();
    }
}