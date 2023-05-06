package ar.com.ariel17.ontop.adapters.http;

import ar.com.ariel17.ontop.adapters.http.entities.requests.TransferRequest;
import ar.com.ariel17.ontop.adapters.http.entities.responses.TransferMovement;
import ar.com.ariel17.ontop.adapters.http.entities.responses.TransferResponse;
import ar.com.ariel17.ontop.core.domain.*;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

@AllArgsConstructor
public class TransferMapper {

    private Currency defaultCurrency;

    public BankAccountOwner bankAccountOwnerFromTransferRequest(TransferRequest request) {
        Long ownerId = request.getRecipientId();
        if (ownerId != null) {
            return BankAccountOwner.builder().
                    id(ownerId).
                    userId(request.getUserId()).
                    build();
        }

        Currency currency = request.getRecipient().getAccount().getCurrency();
        if (currency == null) {
            currency = defaultCurrency;
        }

        BankAccount account = BankAccount.builder().
                routing(request.getRecipient().getAccount().getRoutingNumber()).
                account(request.getRecipient().getAccount().getAccountNumber()).
                currency(currency).
                build();

        return BankAccountOwner.builder().
                userId(request.getUserId()).
                bankAccount(account).
                idNumber(request.getRecipient().getIdNumber()).
                firstName(request.getRecipient().getFirstName()).
                lastName(request.getRecipient().getLastName()).
                build();
    }

    public TransferResponse transactionToTransferResponse(Long userId, Transaction transaction) {
        BigDecimal total = transaction.total();

        Operation operation;
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            operation = Operation.WITHDRAW;
        } else if (total.compareTo(BigDecimal.ZERO) > 0) {
            operation = Operation.TOP_UP;
        } else {
            operation = Operation.REVERT;
        }

        List<TransferMovement> movements = new ArrayList<>(){{
            transaction.getMovements().forEach(tm -> {
                add(TransferMovement.builder().
                        id(tm.getId()).
                        type(tm.getType()).
                        operation(tm.getOperation()).
                        currency(tm.getCurrency()).
                        amount(tm.getAmount()).
                        createdAt(tm.getCreatedAt()).
                        build());
            });
        }};

        PaymentStatus paymentStatus;
        if (transaction.getPayment() != null) {
            paymentStatus = transaction.getPayment().getStatus();
        } else {
            paymentStatus = PaymentStatus.FAILED;
        }

        return TransferResponse.builder().
                userId(userId).
                status(paymentStatus).
                operation(operation).
                movements(movements).
                recipientId(transaction.getExternalAccount().getId()).
                build();
    }
}
