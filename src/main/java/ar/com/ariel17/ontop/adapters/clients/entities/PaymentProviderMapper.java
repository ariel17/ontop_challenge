package ar.com.ariel17.ontop.adapters.clients.entities;

import ar.com.ariel17.ontop.adapters.clients.entities.requests.PaymentProviderAccount;
import ar.com.ariel17.ontop.adapters.clients.entities.requests.PaymentProviderOwner;
import ar.com.ariel17.ontop.adapters.clients.entities.requests.PaymentProviderRequest;
import ar.com.ariel17.ontop.adapters.clients.entities.requests.PaymentProviderSourceInformation;
import ar.com.ariel17.ontop.adapters.clients.entities.responses.PaymentProviderPaymentInfo;
import ar.com.ariel17.ontop.adapters.clients.entities.responses.PaymentProviderRequestInfo;
import ar.com.ariel17.ontop.adapters.clients.entities.responses.PaymentProviderResponse;
import ar.com.ariel17.ontop.core.domain.BankAccountOwner;
import ar.com.ariel17.ontop.core.domain.Payment;

import java.math.BigDecimal;

public class PaymentProviderMapper {

    public PaymentProviderRequest toPaymentProviderRequest(BankAccountOwner from, BankAccountOwner to, BigDecimal amount) {
        PaymentProviderAccount sourceAccount = PaymentProviderAccount.builder().
                accountNumber(from.getBankAccount().getAccount()).
                currency(from.getBankAccount().getCurrency()).
                routingNumber(from.getBankAccount().getRouting()).
                build();

        PaymentProviderSourceInformation sourceInformation = PaymentProviderSourceInformation.builder().
                name(from.getName()).
                build();

        PaymentProviderOwner source = PaymentProviderOwner.builder().
                type(from.getBankAccount().getType().getType()).
                sourceInformation(sourceInformation).
                account(sourceAccount).
                build();

        PaymentProviderAccount destinationAccount = PaymentProviderAccount.builder().
                accountNumber(to.getBankAccount().getAccount()).
                currency(to.getBankAccount().getCurrency()).
                routingNumber(to.getBankAccount().getRouting()).
                build();

        PaymentProviderOwner destination = PaymentProviderOwner.builder().
                name(to.getName()).
                account(destinationAccount).
                build();

        return PaymentProviderRequest.builder().
                source(source).
                destination(destination).
                amount(amount).
                build();
    }

    public Payment paymentProviderResponseToPayment(PaymentProviderResponse response) {
        PaymentProviderPaymentInfo paymentInfo = response.getPaymentInfo();
        PaymentProviderRequestInfo requestInfo = response.getRequestInfo();
        return new Payment(paymentInfo.getId(), paymentInfo.getAmount(), requestInfo.getStatus(), requestInfo.getError(), null);
    }
}
