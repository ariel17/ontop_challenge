package ar.com.ariel17.ontop.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * Payment contains the external provider's operation result.
 */
@AllArgsConstructor
@Builder
@Data
public class Payment implements Serializable {

    private UUID id;

    private BigDecimal amount;

    private PaymentStatus status;

    private String error;

    private Date createdAt;

    public boolean isError() {
        return !StringUtils.isEmpty(error);
    }
}
