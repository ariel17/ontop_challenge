package ar.com.ariel17.ontop.adapters.http;

import ar.com.ariel17.ontop.adapters.http.entities.requests.TransferAccount;
import ar.com.ariel17.ontop.adapters.http.entities.requests.TransferRecipient;
import ar.com.ariel17.ontop.adapters.http.entities.requests.TransferRequest;
import ar.com.ariel17.ontop.adapters.http.entities.responses.TransferMovement;
import ar.com.ariel17.ontop.adapters.http.entities.responses.TransferResponse;
import ar.com.ariel17.ontop.core.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TransferMapperTest {

    private TransferMapper mapper;

    private Currency currency;

    private TransferRequest transferRequest;

    private Transaction transaction;

    private Long userId;

    private BankAccount account1;

    private BankAccount account2;

    @BeforeEach
    public void setUp() {
        currency = Currency.getInstance("USD");
        mapper = new TransferMapper(currency);
        userId = 10L;

        account1 = new BankAccount("0123456789", "012345678", currency);
        account2 = new BankAccount("9876543210", "876543210", currency);
        Payment payment = Payment.builder().id(UUID.randomUUID()).build();

        transaction = new Transaction();
        transaction.setWalletTransactionId(1234L);
        transaction.setPayment(payment);
    }

    @Test
    public void testBankAccountOwnerFromTransferRequest_onlyRecipientId() {
        // TODO assert not null, not only equals
        transferRequest = TransferRequest.builder().recipientId(1234L).build();
        BankAccountOwner owner = mapper.bankAccountOwnerFromTransferRequest(transferRequest);
        assertEquals(transferRequest.getRecipientId(), owner.getId());
        assertEquals(transferRequest.getUserId(), owner.getUserId());
        assertNull(owner.getBankAccount());
        assertNull(owner.getIdNumber());
        assertNull(owner.getFirstName());
        assertNull(owner.getLastName());
    }

    @Test
    public void testBankAccountOwnerFromTransferRequest_withRecipient_withCurrency() {
        testBankAccountOwnerFromTransferRequest_withRecipient(true);
    }

    @Test
    public void testBankAccountOwnerFromTransferRequest_withRecipient_withoutCurrency() {
        testBankAccountOwnerFromTransferRequest_withRecipient(false);
    }

    @Test
    public void testTransactionToTransferResponse_withdraw() {
        transaction.addMovement(Movement.builder().
                id(111L).
                userId(userId).
                type(Type.TRANSFER).
                operation(Operation.WITHDRAW).
                currency(currency).
                amount(new BigDecimal(-1234)).
                from(account1).
                to(account2).
                createdAt(new Date()).
                build());
        transaction.addMovement(Movement.builder().
                id(222L).
                userId(userId).
                type(Type.FEE).
                operation(Operation.WITHDRAW).
                currency(currency).
                amount(new BigDecimal(-5678)).
                createdAt(new Date()).
                build());

        testTransferResponse(Operation.WITHDRAW);
    }

    @Test
    public void testTransactionToTransferResponse_topup() {
        transaction.addMovement(Movement.builder().
                id(111L).
                userId(userId).
                type(Type.TRANSFER).
                operation(Operation.TOP_UP).
                currency(currency).
                amount(new BigDecimal(1234)).
                from(account1).
                to(account2).
                createdAt(new Date()).
                build());
        transaction.addMovement(Movement.builder().
                id(222L).
                userId(userId).
                type(Type.FEE).
                operation(Operation.TOP_UP).
                currency(currency).
                amount(new BigDecimal(5678)).
                createdAt(new Date()).
                build());

        testTransferResponse(Operation.TOP_UP);
    }

    @Test
    public void testTransactionToTransferResponse_revert() {
        transaction.addMovement(Movement.builder().
                id(111L).
                userId(userId).
                type(Type.TRANSFER).
                operation(Operation.WITHDRAW).
                currency(currency).
                amount(new BigDecimal(-1234)).
                from(account1).
                to(account2).
                createdAt(new Date()).
                build());
        transaction.addMovement(Movement.builder().
                id(111L).
                userId(userId).
                type(Type.TRANSFER).
                operation(Operation.REVERT).
                currency(currency).
                amount(new BigDecimal(1234)).
                from(account1).
                to(account2).
                createdAt(new Date()).
                build());
        transaction.addMovement(Movement.builder().
                id(222L).
                userId(userId).
                type(Type.FEE).
                operation(Operation.WITHDRAW).
                currency(currency).
                amount(new BigDecimal(-5678)).
                createdAt(new Date()).
                build());
        transaction.addMovement(Movement.builder().
                id(222L).
                userId(userId).
                type(Type.FEE).
                operation(Operation.REVERT).
                currency(currency).
                amount(new BigDecimal(5678)).
                createdAt(new Date()).
                build());
        testTransferResponse(Operation.REVERT);
    }

    private void testBankAccountOwnerFromTransferRequest_withRecipient(boolean withCurrency) {
        // TODO assert not null, not only equals
        TransferAccount.TransferAccountBuilder builder = TransferAccount.builder().
                routingNumber("0123456789").
                accountNumber("012345678");

        if (withCurrency) {
            builder.currency(currency);
        }

        TransferAccount account = builder.build();

        TransferRecipient recipient = TransferRecipient.builder().
                account(account).
                idNumber("123ABC").
                firstName("John").
                lastName("Snow").
                build();

        transferRequest = TransferRequest.builder().
                recipient(recipient).
                build();

        BankAccountOwner owner = mapper.bankAccountOwnerFromTransferRequest(transferRequest);
        assertNull(owner.getId());
        assertEquals(owner.getUserId(), transferRequest.getUserId());
        assertEquals(owner.getBankAccount().getAccount(), transferRequest.getRecipient().getAccount().getAccountNumber());
        assertEquals(owner.getBankAccount().getRouting(), transferRequest.getRecipient().getAccount().getRoutingNumber());

        if (withCurrency) {
            assertEquals(owner.getBankAccount().getCurrency(), transferRequest.getRecipient().getAccount().getCurrency());
        } else {
            assertEquals(owner.getBankAccount().getCurrency(), currency);
        }

        assertEquals(owner.getIdNumber(), transferRequest.getRecipient().getIdNumber());
        assertEquals(owner.getFirstName(), transferRequest.getRecipient().getFirstName());
        assertEquals(owner.getLastName(), transferRequest.getRecipient().getLastName());
    }

    public void testTransferResponse(Operation operation) {
        // TODO assert not null, not only equals
        TransferResponse response = mapper.transactionToTransferResponse(userId, transaction);
        assertEquals(response.getUserId(), userId);
        assertEquals(response.getStatus(), transaction.getPayment().getStatus());
        assertEquals(response.getOperation(), operation);
        assertEquals(response.getMovements().size(), transaction.getMovements().size());

        for (int i = 0; i < response.getMovements().size(); i++) {
            TransferMovement m1 = response.getMovements().get(i);
            Movement m2 = transaction.getMovements().get(i);
            assertEquals(m1.getId(), m2.getId());
            assertEquals(m1.getType(), m2.getType());
            assertEquals(m1.getOperation(), m2.getOperation());
            assertEquals(m1.getCurrency(), m2.getCurrency());
            assertEquals(m1.getAmount(), m2.getAmount());
            assertEquals(m1.getCreatedAt(), m2.getCreatedAt());
        }
    }
}
