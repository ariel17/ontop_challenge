package ar.com.ariel17.core.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * Payment contains the external provider's operation result.
 */
@Validated
@AllArgsConstructor
@Data
public class Payment {

    @NotNull(message = "ID cannot be null")
    private UUID id;

    @NotNull(message = "Amount cannot be null")
    private BigDecimal amount;

    @NotNull(message = "Status cannot be null")
    private String status;

    private String error;

    private Date createdAt;

    public boolean isError() {
        return !StringUtils.isEmpty(error);
    }
}
