package ar.com.ariel17.adapters.clients;

import ar.com.ariel17.core.clients.PaymentProviderApi;
import ar.com.ariel17.core.clients.PaymentProviderApiException;
import ar.com.ariel17.core.domain.BankAccountOwner;
import ar.com.ariel17.core.domain.Payment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentProviderApiImpl implements PaymentProviderApi {

    @Override
    public Payment createPayment(BankAccountOwner from, BankAccountOwner to, BigDecimal amount) throws PaymentProviderApiException {
        return null;
    }
}
