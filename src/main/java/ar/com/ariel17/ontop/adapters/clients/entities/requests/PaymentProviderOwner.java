package ar.com.ariel17.ontop.adapters.clients.entities.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Payment provider API body for owner data.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonInclude(Include.NON_NULL)
public class PaymentProviderOwner {

    private String type;

    private String name;

    private PaymentProviderSourceInformation sourceInformation;

    private PaymentProviderAccount account;
}
