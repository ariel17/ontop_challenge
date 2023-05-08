package ar.com.ariel17.ontop.adapters.clients.entities.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payment provider source body data.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PaymentProviderSourceInformation {

    private String name;
}
