package ar.com.ariel17.core.clients;

import ar.com.ariel17.core.domain.Payment;
import ar.com.ariel17.core.domain.BankAccountOwner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public interface PaymentProviderApi {

    Payment createPayment(BankAccountOwner from, BankAccountOwner to, BigDecimal amount) throws PaymentProviderApiException;
}
