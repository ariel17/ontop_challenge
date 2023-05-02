package ar.com.ariel17.core.clients.payment;

import ar.com.ariel17.core.domain.Payment;
import ar.com.ariel17.core.domain.bank.BankAccountOwner;

import java.math.BigDecimal;

public interface PaymentProviderApi {

    Payment createPayment(BankAccountOwner from, BankAccountOwner to, BigDecimal amount) throws PaymentProviderApiException;
}