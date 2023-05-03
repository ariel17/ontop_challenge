package ar.com.ariel17.ontop.adapters.clients.entities.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PaymentProviderRequest {

    private PaymentProviderOwner source;

    private PaymentProviderOwner destination;

    private BigDecimal amount;
}
