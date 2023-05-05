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

import static org.junit.jupiter.api.Assertions.*;

public class TransferMapperTest {

    private TransferMapper mapper;

    private Currency currency;

    private TransferRequest transferRequest;

    private Transaction transaction;

    private Long userId;

    private BankAccountOwner onTopAccount;

    private BankAccountOwner externalAccount;

    @BeforeEach
    public void setUp() {
        currency = Currency.getInstance("USD");
        mapper = new TransferMapper(currency);
        userId = 10L;

        BankAccount account = BankAccount.builder().
                routing("0123456789").
                account("012345678").
                type(BankAccountType.COMPANY).
                currency(Currency.getInstance("USD")).build();
        onTopAccount = BankAccountOwner.builder().
                id(1L).
                userId(10L).
                bankAccount(account).
                firstName("ONTOP INC").build();

        account = BankAccount.builder().
                routing("9876543210").
                account("876543210").
                type(BankAccountType.COMPANY).
                currency(Currency.getInstance("USD")).build();
        externalAccount = BankAccountOwner.builder().
                id(2L).
                userId(10L).
                bankAccount(account).
                idNumber("123ABC").
                firstName("John").
                lastName("Snow").build();

        Payment payment = Payment.builder().
                id(UUID.randomUUID()).
                status("error").
                error("Error description").
                createdAt(new Date()).
                build();

        transaction = new Transaction();
        transaction.setWalletTransactionId(1234L);
        transaction.setPayment(payment);
        transaction.setExternalAccount(externalAccount);
    }

    @Test
    public void testBankAccountOwnerFromTransferRequest_onlyRecipientId() {
        transferRequest = TransferRequest.builder().
                userId(10L).
                recipientId(1234L).
                currency(currency).
                amount(new BigDecimal(1000)).
                build();

        BankAccountOwner owner = mapper.bankAccountOwnerFromTransferRequest(transferRequest);

        assertNotNull(owner.getId());
        assertEquals(transferRequest.getRecipientId(), owner.getId());

        assertNotNull(owner.getUserId());
        assertEquals(transferRequest.getUserId(), owner.getUserId());

        assertNull(owner.getBankAccount());
        assertNull(owner.getIdNumber());
        assertNull(owner.getFirstName());
        assertNull(owner.getLastName());
        assertNull(owner.getCreatedAt());
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
                type(MovementType.TRANSFER).
                operation(Operation.WITHDRAW).
                currency(currency).
                amount(new BigDecimal(-1234)).
                onTopAccount(onTopAccount).
                externalAccount(externalAccount).
                createdAt(new Date()).
                build());
        transaction.addMovement(Movement.builder().
                id(222L).
                userId(userId).
                type(MovementType.FEE).
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
                type(MovementType.TRANSFER).
                operation(Operation.TOP_UP).
                currency(currency).
                amount(new BigDecimal(1234)).
                onTopAccount(onTopAccount).
                externalAccount(externalAccount).
                createdAt(new Date()).
                build());
        transaction.addMovement(Movement.builder().
                id(222L).
                userId(userId).
                type(MovementType.FEE).
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
                type(MovementType.TRANSFER).
                operation(Operation.WITHDRAW).
                currency(currency).
                amount(new BigDecimal(-1234)).
                onTopAccount(onTopAccount).
                externalAccount(externalAccount).
                createdAt(new Date()).
                build());
        transaction.addMovement(Movement.builder().
                id(111L).
                userId(userId).
                type(MovementType.TRANSFER).
                operation(Operation.REVERT).
                currency(currency).
                amount(new BigDecimal(1234)).
                onTopAccount(onTopAccount).
                externalAccount(externalAccount).
                createdAt(new Date()).
                build());
        transaction.addMovement(Movement.builder().
                id(222L).
                userId(userId).
                type(MovementType.FEE).
                operation(Operation.WITHDRAW).
                currency(currency).
                amount(new BigDecimal(-5678)).
                createdAt(new Date()).
                build());
        transaction.addMovement(Movement.builder().
                id(222L).
                userId(userId).
                type(MovementType.FEE).
                operation(Operation.REVERT).
                currency(currency).
                amount(new BigDecimal(5678)).
                createdAt(new Date()).
                build());
        testTransferResponse(Operation.REVERT);
    }

    private void testBankAccountOwnerFromTransferRequest_withRecipient(boolean withCurrency) {
        TransferAccount.TransferAccountBuilder accountBuilder = TransferAccount.builder().
                routingNumber("0123456789").
                accountNumber("012345678");

        if (withCurrency) {
            accountBuilder.currency(currency);
        }

        TransferAccount account = accountBuilder.build();

        TransferRecipient recipient = TransferRecipient.builder().
                account(account).
                idNumber("123ABC").
                firstName("John").
                lastName("Snow").
                build();

        TransferRequest.TransferRequestBuilder transferBuilder = TransferRequest.builder().
                userId(userId).
                recipient(recipient).
                amount(new BigDecimal(1000));

       if (withCurrency) {
           transferBuilder.currency(currency);
       }

        transferRequest = transferBuilder.build();

        BankAccountOwner owner = mapper.bankAccountOwnerFromTransferRequest(transferRequest);

        assertNull(owner.getId());

        assertNotNull(owner.getUserId());
        assertEquals(owner.getUserId(), transferRequest.getUserId());

        assertNotNull(owner.getBankAccount());

        assertNotNull(owner.getBankAccount().getRouting());
        assertEquals(owner.getBankAccount().getRouting(), transferRequest.getRecipient().getAccount().getRoutingNumber());

        assertNotNull(owner.getBankAccount().getAccount());
        assertEquals(owner.getBankAccount().getAccount(), transferRequest.getRecipient().getAccount().getAccountNumber());

        assertNotNull(owner.getBankAccount().getCurrency());
        if (withCurrency) {
            assertEquals(owner.getBankAccount().getCurrency(), transferRequest.getRecipient().getAccount().getCurrency());
        } else {
            assertEquals(owner.getBankAccount().getCurrency(), currency);
        }

        assertNotNull(owner.getIdNumber());
        assertEquals(owner.getIdNumber(), transferRequest.getRecipient().getIdNumber());

        assertNotNull(owner.getFirstName());
        assertEquals(owner.getFirstName(), transferRequest.getRecipient().getFirstName());

        assertNotNull(owner.getLastName());
        assertEquals(owner.getLastName(), transferRequest.getRecipient().getLastName());

        assertNull(owner.getCreatedAt());
    }

    public void testTransferResponse(Operation operation) {
        TransferResponse response = mapper.transactionToTransferResponse(userId, transaction);

        assertNotNull(response.getUserId());
        assertEquals(response.getUserId(), userId);

        assertNotNull(response.getStatus());
        assertEquals(response.getStatus(), transaction.getPayment().getStatus());

        assertNotNull(response.getOperation());
        assertEquals(response.getOperation(), operation);

        assertNotNull(response.getRecipientId());
        assertNotNull(transaction.getExternalAccount().getId());
        assertEquals(response.getRecipientId(), transaction.getExternalAccount().getId());

        assertEquals(response.getMovements().size(), transaction.getMovements().size());

        for (int i = 0; i < response.getMovements().size(); i++) {
            TransferMovement m1 = response.getMovements().get(i);
            Movement m2 = transaction.getMovements().get(i);

            assertNotNull(m1.getId());
            assertNotNull(m2.getId());
            assertEquals(m1.getId(), m2.getId());

            assertNotNull(m1.getType());
            assertNotNull(m2.getType());
            assertEquals(m1.getType(), m2.getType());

            assertNotNull(m1.getOperation());
            assertNotNull(m2.getOperation());
            assertEquals(m1.getOperation(), m2.getOperation());

            assertNotNull(m1.getCurrency());
            assertNotNull(m2.getCurrency());
            assertEquals(m1.getCurrency(), m2.getCurrency());

            assertNotNull(m1.getAmount());
            assertNotNull(m2.getAmount());
            assertEquals(m1.getAmount(), m2.getAmount());

            assertNotNull(m1.getCreatedAt());
            assertNotNull(m2.getCreatedAt());
            assertEquals(m1.getCreatedAt(), m2.getCreatedAt());
        }
    }
}
