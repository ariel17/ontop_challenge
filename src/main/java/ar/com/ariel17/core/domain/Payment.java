package ar.com.ariel17.core.domain;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * Payment contains the external provider's operation result.
 */
public class Payment extends BaseModel<UUID> {

    private BigDecimal amount;

    private String status;

    private String error;

    /**
     * Creates a new payment.
     *
     * @param id The UUID for this transaction.
     * @param amount The amount of money transferred.
     * @param status The operation status as text.
     * @param createdAt The creation date and time. If null, the payment was not yet stored.
     */
    public Payment(@NotNull UUID id, @NotNull BigDecimal amount, @NotNull String status, String error, @NotNull Date createdAt) {
        this.id = id;
        this.amount = amount;
        this.status = status;
        this.error = error;
        this.createdAt = createdAt;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }
}
