package ar.com.ariel17.ontop.adapters.clients.entities.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payment provider response. It works on successful and on error responses.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentProviderResponse {

    @JsonProperty("requestInfo")
    private PaymentProviderRequestInfo requestInfo;

    @JsonProperty("paymentInfo")
    private PaymentProviderPaymentInfo paymentInfo;
}
