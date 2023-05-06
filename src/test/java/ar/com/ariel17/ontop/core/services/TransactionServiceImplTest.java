package ar.com.ariel17.ontop.core.services;

import ar.com.ariel17.ontop.core.clients.*;
import ar.com.ariel17.ontop.core.domain.*;
import ar.com.ariel17.ontop.core.repositories.BankAccountRepository;
import ar.com.ariel17.ontop.core.repositories.LockRepository;
import ar.com.ariel17.ontop.core.repositories.MovementRepository;
import ar.com.ariel17.ontop.core.repositories.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

    @Mock
    private ApplicationContext context;

    @Mock
    private BankAccountRepository bankAccountRepository;

    private BankAccountOwner onTopAccount;

    @Mock
    private WalletApiClient walletAPIClient;

    @Mock
    private PaymentProviderApiClient paymentProviderAPIClient;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private MovementRepository movementRepository;

    private TransactionServiceImpl service;

    private Long userId;

    private BankAccountOwner recipient;

    private BigDecimal amount;

    @Mock
    private LockRepository lockRepository;

    @BeforeEach
    public void setUp() {
        Currency currency = Currency.getInstance("USD");
        BankAccount account = BankAccount.builder().
                routing("0123456789").
                account("012345678").
                type(BankAccountType.COMPANY).
                currency(currency).build();
        onTopAccount = BankAccountOwner.builder().
                userId(0L).bankAccount(account).firstName("ONTOP INC").build();

        BigDecimal feePercent = new BigDecimal("0.1");
        TransactionFactory transactionFactory = new TransactionFactory(feePercent);

        service = new TransactionServiceImpl(context, bankAccountRepository, onTopAccount, transactionFactory, walletAPIClient, paymentProviderAPIClient, paymentRepository, movementRepository);

        userId = 10L;

        account = BankAccount.builder().
                routing("0123456789").
                account("012345678").
                type(BankAccountType.COMPANY).
                currency(Currency.getInstance("USD")).build();
        recipient = BankAccountOwner.builder().
                userId(userId).
                bankAccount(account).
                idNumber("123ABC").
                firstName("John").
                lastName("Snow").build();

        amount = new BigDecimal(1000);
    }

    @Test
    public void testTransfer_ok() throws Exception {
        when(context.getBean(eq(LockRepository.class), eq(userId))).thenReturn(lockRepository);
        when(lockRepository.acquire()).thenReturn(true);
        when(bankAccountRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(walletAPIClient.getBalance(eq(userId))).thenReturn(new BigDecimal(5000));

        BigDecimal total = BigDecimal.valueOf(-1100.0);
        Long walletTransactionId = 555L;
        when(walletAPIClient.createTransaction(eq(userId), eq(total))).thenReturn(walletTransactionId);

        UUID paymentId = UUID.randomUUID();
        Payment payment = new Payment(paymentId, new BigDecimal(3999), PaymentStatus.PROCESSING, null, null);
        when(paymentProviderAPIClient.createPayment(eq(onTopAccount), eq(recipient), eq(amount))).thenReturn(payment);

        when(paymentRepository.save(eq(payment))).thenAnswer(i -> i.getArguments()[0]);

        when(movementRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);

        Transaction transaction = service.transfer(userId, recipient, amount);

        verify(bankAccountRepository, times(1)).save(eq(recipient));

        verify(lockRepository, times(1)).close();

        transaction.getMovements().forEach(m -> {
            assertEquals(m.getWalletTransactionId(), walletTransactionId);
            assertEquals(payment, m.getPayment());
        });
    }

    @Test
    public void testTransfer_recipientRepositoryFails() {
        doThrow(new RuntimeException("mocked exception")).when(bankAccountRepository).save(eq(recipient));

        assertThrows(TransactionException.class, () -> service.transfer(userId, recipient, amount));
    }

    @Test
    public void testTransfer_lockNotAcquired() throws Exception {
        when(bankAccountRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(context.getBean(eq(LockRepository.class), eq(userId))).thenReturn(lockRepository);
        when(lockRepository.acquire()).thenReturn(false);

        assertThrows(TransactionException.class, () -> service.transfer(userId, recipient, amount));

        verify(lockRepository, times(1)).close();
    }

    @Test
    public void testTransfer_insufficientBalance() throws Exception {
        when(bankAccountRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(context.getBean(eq(LockRepository.class), eq(userId))).thenReturn(lockRepository);
        when(lockRepository.acquire()).thenReturn(true);
        when(walletAPIClient.getBalance(eq(userId))).thenReturn(new BigDecimal(200));

        assertThrows(InsufficientBalanceException.class, () -> service.transfer(userId, recipient, amount));

        verify(lockRepository, times(1)).close();
    }

    @Test
    public void testTransfer_getBalanceFails() throws Exception {
        when(bankAccountRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(context.getBean(eq(LockRepository.class), eq(userId))).thenReturn(lockRepository);
        when(lockRepository.acquire()).thenReturn(true);
        doThrow(new WalletApiException("mocked exception")).when(walletAPIClient).getBalance(eq(userId));

        assertThrows(TransactionException.class, () -> service.transfer(userId, recipient, amount));

        verify(lockRepository, times(1)).close();
    }

    @Test
    public void testTransfer_getBalanceFailsByUserNotFound() throws Exception {
        when(bankAccountRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(context.getBean(eq(LockRepository.class), eq(userId))).thenReturn(lockRepository);
        when(lockRepository.acquire()).thenReturn(true);
        doThrow(new UserNotFoundException("not found")).when(walletAPIClient).getBalance(eq(userId));

        assertThrows(UserNotFoundException.class, () -> service.transfer(userId, recipient, amount));

        verify(lockRepository, times(1)).close();
    }

    @Test
    public void testTransfer_walletTransactionFails() throws Exception {
        when(bankAccountRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(context.getBean(eq(LockRepository.class), eq(userId))).thenReturn(lockRepository);
        when(lockRepository.acquire()).thenReturn(true);
        when(walletAPIClient.getBalance(eq(userId))).thenReturn(new BigDecimal(5000));

        BigDecimal total = BigDecimal.valueOf(-1100.0);
        doThrow(new WalletApiException("mocked exception")).when(walletAPIClient).createTransaction(eq(userId), eq(total));

        assertThrows(TransactionException.class, () -> service.transfer(userId, recipient, amount));

        verify(lockRepository, times(1)).close();
    }

    @Test
    public void testTransfer_walletTransactionFailsByUserNotFound() throws Exception {
        when(bankAccountRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(context.getBean(eq(LockRepository.class), eq(userId))).thenReturn(lockRepository);
        when(lockRepository.acquire()).thenReturn(true);
        when(walletAPIClient.getBalance(eq(userId))).thenReturn(new BigDecimal(5000));

        BigDecimal total = BigDecimal.valueOf(-1100.0);
        doThrow(new UserNotFoundException("not found")).when(walletAPIClient).createTransaction(eq(userId), eq(total));

        assertThrows(UserNotFoundException.class, () -> service.transfer(userId, recipient, amount));

        verify(lockRepository, times(1)).close();
    }

    @Test
    public void testTransfer_paymentProviderFails() throws Exception {
        when(bankAccountRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(context.getBean(eq(LockRepository.class), eq(userId))).thenReturn(lockRepository);
        when(lockRepository.acquire()).thenReturn(true);
        when(walletAPIClient.getBalance(eq(userId))).thenReturn(new BigDecimal(5000));
        when(movementRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);

        BigDecimal total = BigDecimal.valueOf(-1100.0);
        Long walletTransactionId = 555L;
        when(walletAPIClient.createTransaction(eq(userId), eq(total))).thenReturn(walletTransactionId);

        doThrow(new PaymentProviderApiException("mocked error")).when(paymentProviderAPIClient).createPayment(eq(onTopAccount), eq(recipient), eq(amount));
        Transaction transaction = service.transfer(userId, recipient, amount);

        assertEquals(4, transaction.getMovements().size());
        assertEquals(0, BigDecimal.ZERO.compareTo(transaction.total()));

        validateRevertedMovements(MovementType.FEE, transaction, true);
        validateRevertedMovements(MovementType.TRANSFER, transaction, true);

        verify(walletAPIClient, times(1)).createTransaction(eq(userId), eq(total));
        verify(walletAPIClient, times(1)).createTransaction(eq(userId), eq(total.negate()));

        verify(lockRepository, times(1)).close();
    }

    @Test
    public void testTransfer_paymentProviderResponseIsError() throws Exception {
        when(bankAccountRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(context.getBean(eq(LockRepository.class), eq(userId))).thenReturn(lockRepository);
        when(lockRepository.acquire()).thenReturn(true);
        when(walletAPIClient.getBalance(eq(userId))).thenReturn(new BigDecimal(5000));
        when(movementRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);

        BigDecimal total = BigDecimal.valueOf(-1100.0);
        Long walletTransactionId = 555L;
        when(walletAPIClient.createTransaction(eq(userId), eq(total))).thenReturn(walletTransactionId);

        UUID paymentId = UUID.randomUUID();
        Payment response = new Payment(paymentId, new BigDecimal(3999), PaymentStatus.FAILED, "error", null);
        when(paymentProviderAPIClient.createPayment(eq(onTopAccount), eq(recipient), eq(amount))).thenReturn(response);

        when(paymentRepository.save(eq(response))).thenAnswer(i -> i.getArguments()[0]);

        Transaction transaction = service.transfer(userId, recipient, amount);

        assertEquals(4, transaction.getMovements().size());
        assertEquals(0, BigDecimal.ZERO.compareTo(transaction.total()));

        validateRevertedMovements(MovementType.FEE, transaction, false);
        validateRevertedMovements(MovementType.TRANSFER, transaction, false);

        verify(walletAPIClient, times(1)).createTransaction(eq(userId), eq(total));
        verify(walletAPIClient, times(1)).createTransaction(eq(userId), eq(total.negate()));

        verify(lockRepository, times(1)).close();
    }

    @Test
    public void testTransfer_paymentProviderFailsAndLaterWalletFails() throws Exception {
        when(bankAccountRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(context.getBean(eq(LockRepository.class), eq(userId))).thenReturn(lockRepository);
        when(lockRepository.acquire()).thenReturn(true);
        when(walletAPIClient.getBalance(eq(userId))).thenReturn(new BigDecimal(5000));

        BigDecimal total = BigDecimal.valueOf(-1100.0);
        Long walletTransactionId = 555L;
        when(walletAPIClient.createTransaction(eq(userId), eq(total))).thenReturn(walletTransactionId);

        doThrow(new PaymentProviderApiException("mocked provider error")).when(paymentProviderAPIClient).createPayment(eq(onTopAccount), eq(recipient), eq(amount));

        doThrow(new WalletApiException("mocked wallet exception")).when(walletAPIClient).createTransaction(eq(userId), eq(total.negate()));

        assertThrows(TransactionException.class, () -> service.transfer(userId, recipient, amount));

        verify(walletAPIClient, times(1)).createTransaction(eq(userId), eq(total));
        verify(walletAPIClient, times(1)).createTransaction(eq(userId), eq(total.negate()));

        verify(lockRepository, times(1)).close();
    }

    @Test
    public void testTransfer_movementRepositoryFails() throws Exception {
        when(bankAccountRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(context.getBean(eq(LockRepository.class), eq(userId))).thenReturn(lockRepository);
        when(lockRepository.acquire()).thenReturn(true);
        when(walletAPIClient.getBalance(eq(userId))).thenReturn(new BigDecimal(5000));

        BigDecimal total = BigDecimal.valueOf(-1100.0);
        Long walletTransactionId = 555L;
        when(walletAPIClient.createTransaction(eq(userId), eq(total))).thenReturn(walletTransactionId);

        UUID paymentId = UUID.randomUUID();
        Payment response = new Payment(paymentId, new BigDecimal(3999), PaymentStatus.PROCESSING, null, null);
        when(paymentProviderAPIClient.createPayment(eq(onTopAccount), eq(recipient), eq(amount))).thenReturn(response);

        when(paymentRepository.save(eq(response))).thenAnswer(i -> i.getArguments()[0]);

        Mockito.doThrow(new RuntimeException("mocked exception")).when(movementRepository).save(any(Transaction.class));

        assertThrows(TransactionException.class, () -> service.transfer(userId, recipient, amount));

        verify(bankAccountRepository, times(1)).save(eq(recipient));

        verify(lockRepository, times(1)).close();
    }

    private void validateRevertedMovements(MovementType type, Transaction transaction, boolean paymentFailed) {
        AtomicInteger total = new AtomicInteger();
        AtomicInteger totalWithdraw = new AtomicInteger();
        AtomicInteger totalRevert = new AtomicInteger();

        transaction.getMovements().stream().filter(m -> m.getType() == type).forEach(m -> {
            total.getAndIncrement();
            switch (m.getOperation()) {
                case WITHDRAW -> totalWithdraw.getAndIncrement();
                case REVERT -> totalRevert.getAndIncrement();
            }
            assertNotNull(m.getWalletTransactionId());
            assertEquals(paymentFailed, m.getPayment() == null);
        });

        assertEquals(2, total.get());
        assertEquals(1, totalWithdraw.get());
        assertEquals(1, totalRevert.get());
    }
}
