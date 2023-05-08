package ar.com.ariel17.ontop.core.clients;

import ar.com.ariel17.ontop.core.domain.BankAccountOwner;
import ar.com.ariel17.ontop.core.domain.Payment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Payment provider API client contract for implementors.
 */
@Component
public interface PaymentProviderApiClient {

    Payment createPayment(BankAccountOwner from, BankAccountOwner to, BigDecimal amount) throws PaymentProviderApiException;
}
