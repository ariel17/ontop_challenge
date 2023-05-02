package ar.com.ariel17.core.domain;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * Payment contains the external provider's operation result.
 */
@Validated
public class Payment extends BaseModel<UUID> {

    @NotNull(message="Amount cannot be null")
    private BigDecimal amount;

    @NotNull(message="Status cannot be null")
    private String status;

    private String error;

    /**
     * Creates a new payment.
     *
     * @param id The UUID for this transaction.
     * @param amount The amount of money transferred.
     * @param status The operation status as text.
     * @param error The error description, if any.
     * @param createdAt The creation date and time. If null, the payment was not yet stored.
     */
    public Payment(UUID id, BigDecimal amount, String status, String error, Date createdAt) {
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
