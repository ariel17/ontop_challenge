package ar.com.ariel17.core.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * Movement represents a money displace. Relates the wallet user with a bank
 * account transaction.
 */
public class Movement extends BaseModel<Integer> {

    private String type;

    private String status;

    private String currency;

    private BigDecimal amount;

    private BankAccount from;

    private BankAccount to;

    private UUID paymentId;

    /**
     * Creates a new movement.
     *
     * @param id The unique id for this movement. If null, the movement was not yet stored.
     * @param type
     * @param status
     * @param currency
     * @param amount
     * @param from
     * @param to
     * @param paymentId
     * @param createdAt The movement creation date. If null, the movement was not yet stored.
     */
    public Movement(Integer id, String type, String status, String currency, BigDecimal amount, BankAccount from, BankAccount to, UUID paymentId, Date createdAt) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.currency = currency;
        this.amount = amount;
        this.from = from;
        this.to = to;
        this.paymentId = paymentId;
        this.createdAt = createdAt;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public String getCurrency() {
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

    public void setPaymentId(UUID paymentId) {
        this.paymentId = paymentId;
    }

    public UUID getPaymentId() {
        return paymentId;
    }
}