package ar.com.ariel17.ontop.adapters.clients;

import ar.com.ariel17.ontop.core.clients.PaymentProviderApi;
import ar.com.ariel17.ontop.core.clients.PaymentProviderApiException;
import ar.com.ariel17.ontop.core.domain.BankAccountOwner;
import ar.com.ariel17.ontop.core.domain.Payment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentProviderApiImpl implements PaymentProviderApi {

    @Override
    public Payment createPayment(BankAccountOwner from, BankAccountOwner to, BigDecimal amount) throws PaymentProviderApiException {
        return null;
    }
}