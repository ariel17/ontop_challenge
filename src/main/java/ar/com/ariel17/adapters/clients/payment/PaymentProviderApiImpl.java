package ar.com.ariel17.adapters.clients.payment;

import ar.com.ariel17.core.clients.payment.PaymentProviderApi;
import ar.com.ariel17.core.clients.payment.PaymentProviderApiException;
import ar.com.ariel17.core.domain.Payment;
import ar.com.ariel17.core.domain.bank.BankAccountOwner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentProviderApiImpl implements PaymentProviderApi {

    @Override
    public Payment createPayment(BankAccountOwner from, BankAccountOwner to, BigDecimal amount) throws PaymentProviderApiException {
        return null;
    }
}
