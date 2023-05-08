package ar.com.ariel17.ontop.adapters.http.entities.responses;

import ar.com.ariel17.ontop.core.domain.Operation;
import ar.com.ariel17.ontop.core.domain.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * The REST API transfer operation response. It shows the operation type
 * (withdraw, topup, revert), the status by provider, the recipient used for
 * transfer and the movement details.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransferResponse {

    @JsonProperty("user_id")
    private Long userId;

    private PaymentStatus status;

    private Operation operation;

    @JsonProperty("recipient_id")
    private Long recipientId;

    private List<TransferMovement> movements;
}
