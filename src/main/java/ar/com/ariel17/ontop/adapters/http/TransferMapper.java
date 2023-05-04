package ar.com.ariel17.ontop.adapters.http;

import ar.com.ariel17.ontop.adapters.http.entities.requests.TransferRequest;
import ar.com.ariel17.ontop.adapters.http.entities.responses.TransferMovement;
import ar.com.ariel17.ontop.adapters.http.entities.responses.TransferResponse;
import ar.com.ariel17.ontop.core.domain.BankAccount;
import ar.com.ariel17.ontop.core.domain.BankAccountOwner;
import ar.com.ariel17.ontop.core.domain.Operation;
import ar.com.ariel17.ontop.core.domain.Transaction;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

@AllArgsConstructor
public class TransferMapper {

    private Currency defaultCurrency;

    public BankAccountOwner mapBankAccountOwnerFromRequest(TransferRequest request) {
        Long ownerId = request.getRecipientId();
        if (ownerId != null) {
            return BankAccountOwner.builder().
                    id(ownerId).
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

    public TransferResponse mapTransactionToTransferResponse(Long userId, Transaction transaction) {
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
                        build());
            });
        }};

        return TransferResponse.builder().
                userId(userId).
                status(transaction.getPayment().getStatus()).
                operation(operation).
                movements(movements).
                build();
    }
}