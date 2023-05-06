package ar.com.ariel17.ontop.adapters.http;

import ar.com.ariel17.ontop.adapters.http.entities.requests.TransferAccount;
import ar.com.ariel17.ontop.adapters.http.entities.requests.TransferRecipient;
import ar.com.ariel17.ontop.adapters.http.entities.requests.TransferRequest;
import ar.com.ariel17.ontop.adapters.http.entities.responses.TransferResponse;
import ar.com.ariel17.ontop.core.clients.UserNotFoundException;
import ar.com.ariel17.ontop.core.domain.*;
import ar.com.ariel17.ontop.core.repositories.BankAccountOwnerNotFoundException;
import ar.com.ariel17.ontop.core.services.TransactionException;
import ar.com.ariel17.ontop.core.services.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransferControllerTest {

    private TransferController controller;

    @Mock
    private TransactionService service;

    private Currency currency;

    private TransferRequest request;

    private Transaction transaction;

    private Long userId;

    @BeforeEach
    public void setUp() {
        currency = Currency.getInstance("USD");
        controller = new TransferController(service, currency);

        userId = 10L;

        BankAccount account = BankAccount.builder().
                routing("0123456789").
                account("012345678").
                type(BankAccountType.COMPANY).
                currency(currency).build();
        BankAccountOwner onTopAccount = BankAccountOwner.builder().
                id(1L).
                userId(0L).
                bankAccount(account).
                firstName("ONTOP INC").
                build();

        account = BankAccount.builder().
                routing("9876543210").
                account("876543210").
                currency(currency).build();
        BankAccountOwner externalAccount = BankAccountOwner.builder().
                id(2L).
                userId(10L).
                bankAccount(account).
                idNumber("123ABC").
                firstName("John").
                lastName("Snow").build();

        Payment payment = Payment.builder().
                id(UUID.randomUUID()).
                status(PaymentStatus.PROCESSING).
                createdAt(new Date()).build();

        transaction = new Transaction();
        transaction.addMovement(Movement.builder().
                id(111L).
                userId(userId).
                type(MovementType.TRANSFER).
                operation(Operation.WITHDRAW).
                currency(currency).
                amount(new BigDecimal(-1234)).
                onTopAccount(onTopAccount).
                externalAccount(externalAccount).
                createdAt(new Date()).build());
        transaction.addMovement(Movement.builder().
                id(222L).
                userId(userId).
                type(MovementType.FEE).
                onTopAccount(onTopAccount).
                externalAccount(externalAccount).
                operation(Operation.WITHDRAW).
                currency(currency).
                amount(new BigDecimal(-5678)).
                createdAt(new Date()).build());
        transaction.setWalletTransactionId(1234L);
        transaction.setPayment(payment);
        transaction.setExternalAccount(externalAccount);
    }

    @Test
    public void testCreateTransfer_okRecipientId() throws UserNotFoundException, TransactionException, BankAccountOwnerNotFoundException {
        request = TransferRequest.builder().
                userId(userId).
                recipientId(123L).
                currency(currency).
                amount(new BigDecimal(10000)).
                build();
        when(service.transfer(eq(userId), any(), eq(request.getAmount()))).thenReturn(transaction);

        TransferResponse response = controller.createTransfer(request);
        assertEquals(response.getUserId(), userId);
        assertEquals(response.getOperation(), Operation.WITHDRAW);
        assertEquals(response.getMovements().size(), transaction.getMovements().size());
    }

    @Test
    public void testCreateTransfer_okRecipient() throws UserNotFoundException, TransactionException, BankAccountOwnerNotFoundException {
        TransferAccount account = TransferAccount.builder().
                routingNumber("0123456789").
                accountNumber("012345678").
                currency(currency).
                build();

        TransferRecipient recipient = TransferRecipient.builder().
                account(account).
                idNumber("123ABC").
                firstName("John").
                lastName("Snow").
                build();

        request = TransferRequest.builder().
                userId(userId).
                currency(currency).
                amount(new BigDecimal(10000)).
                recipient(recipient).
                build();

        when(service.transfer(eq(userId), any(), eq(request.getAmount()))).thenReturn(transaction);

        TransferResponse response = controller.createTransfer(request);
        assertEquals(response.getUserId(), userId);
        assertEquals(response.getOperation(), Operation.WITHDRAW);
        assertEquals(response.getStatus(), transaction.getPayment().getStatus());
        assertEquals(response.getRecipientId(), transaction.getExternalAccount().getId());
        assertEquals(response.getMovements().size(), transaction.getMovements().size());
    }

    @Test
    public void testCreateTransfer_invalidRecipient_both() {
        request = TransferRequest.builder().
                recipientId(123L).
                recipient(new TransferRecipient()).
                build();

        Throwable e = assertThrows(ResponseStatusException.class, () -> controller.createTransfer(request));
        assertEquals("400 BAD_REQUEST \"Use recipient_id or recipient, not both\"", e.getMessage());
    }

    @Test
    public void testCreateTransfer_errorUserNotFound() throws UserNotFoundException, TransactionException, BankAccountOwnerNotFoundException {
        request = TransferRequest.builder().
                userId(userId).
                recipientId(123L).
                currency(currency).
                amount(new BigDecimal(10000)).
                build();

        doThrow(new UserNotFoundException("not found")).when(service).transfer(eq(userId), any(), eq(request.getAmount()));

        Throwable e = assertThrows(ResponseStatusException.class, () -> controller.createTransfer(request));
        assertEquals("404 NOT_FOUND \"ar.com.ariel17.ontop.core.clients.UserNotFoundException: not found\"", e.getMessage());
    }

    @Test
    public void testCreateTransfer_errorOwnerNotFound() throws UserNotFoundException, TransactionException, BankAccountOwnerNotFoundException {
        request = TransferRequest.builder().
                userId(userId).
                recipientId(123L).
                currency(currency).
                amount(new BigDecimal(10000)).
                build();

        doThrow(new BankAccountOwnerNotFoundException("not found")).when(service).transfer(eq(userId), any(), eq(request.getAmount()));

        Throwable e = assertThrows(ResponseStatusException.class, () -> controller.createTransfer(request));
        assertEquals("404 NOT_FOUND \"ar.com.ariel17.ontop.core.repositories.BankAccountOwnerNotFoundException: not found\"", e.getMessage());
    }

    @Test
    public void testCreateTransfer_errorTransactionError() throws UserNotFoundException, TransactionException, BankAccountOwnerNotFoundException {
        request = TransferRequest.builder().
                userId(userId).
                recipientId(123L).
                currency(currency).
                amount(new BigDecimal(10000)).
                build();

        doThrow(new TransactionException("blah")).when(service).transfer(eq(userId), any(), eq(request.getAmount()));

        Throwable e = assertThrows(ResponseStatusException.class, () -> controller.createTransfer(request));
        assertEquals("500 INTERNAL_SERVER_ERROR \"ar.com.ariel17.ontop.core.services.TransactionException: blah\"", e.getMessage());
    }
}
