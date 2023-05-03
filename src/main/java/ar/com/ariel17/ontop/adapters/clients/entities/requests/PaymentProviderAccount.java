package ar.com.ariel17.ontop.adapters.clients.entities.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Currency;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PaymentProviderAccount {

    private String accountNumber;

    private Currency currency;

    private String routingNumber;
}
