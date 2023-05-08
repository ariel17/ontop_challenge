package ar.com.ariel17.ontop.adapters.clients.entities.responses;

import ar.com.ariel17.ontop.core.domain.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payment provider request data body.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentProviderRequestInfo {

    private PaymentStatus status;

    private String error;
}
