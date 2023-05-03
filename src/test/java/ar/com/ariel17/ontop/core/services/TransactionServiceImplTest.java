package ar.com.ariel17.ontop.core.services;

import ar.com.ariel17.ontop.core.clients.PaymentProviderApiClient;
import ar.com.ariel17.ontop.core.clients.PaymentProviderApiException;
import ar.com.ariel17.ontop.core.clients.WalletApiClient;
import ar.com.ariel17.ontop.core.clients.WalletApiException;
import ar.com.ariel17.ontop.core.domain.*;
import ar.com.ariel17.ontop.core.repositories.*;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

    @Mock
    private ApplicationContext context;

    @Mock
    private BankAccountRepository bankAccountRepository;

    private BankAccountOwner sourceOwner;

    private TransactionFactory transactionFactory;

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
        BankAccount account = new BankAccount(1234L, 1234L, Currency.getInstance("USD"));
        sourceOwner = new BankAccountOwner(null, 0L, account, "", "ON TOP INC", "", null);

        BigDecimal feePercent = new BigDecimal("0.1");
        transactionFactory = new TransactionFactory(feePercent);

        service = new TransactionServiceImpl(context, bankAccountRepository, sourceOwner, transactionFactory, walletAPIClient, paymentProviderAPIClient, paymentRepository, movementRepository);

        userId = 10L;

        account = new BankAccount(1234L, 1234L, Currency.getInstance("USD"));
        recipient = new BankAccountOwner(null, userId, account, "1234", "John", "Doe", null);
        amount = new BigDecimal(1000);
    }

    @Test
    public void testTransfer_ok() throws Exception {
        when(context.getBean(eq(LockRepository.class), eq(userId))).thenReturn(lockRepository);
        when(lockRepository.acquire()).thenReturn(true);
        when(walletAPIClient.getBalance(eq(userId))).thenReturn(new BigDecimal(5000));

        BigDecimal total = BigDecimal.valueOf(-1100.0);
        Long walletTransactionId = 555L;
        when(walletAPIClient.createTransaction(eq(userId), eq(total))).thenReturn(walletTransactionId);

        UUID paymentId = UUID.randomUUID();
        Payment response = new Payment(paymentId, new BigDecimal(3999), "ok", null, null);
        when(paymentProviderAPIClient.createPayment(eq(sourceOwner), eq(recipient), eq(amount))).thenReturn(response);

        when(movementRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);

        Transaction transaction = service.transfer(userId, recipient, amount);

        verify(bankAccountRepository, times(1)).save(eq(recipient));

        verify(lockRepository, times(1)).close();

        for (Movement m : transaction.getMovements()) {
            assertEquals(m.getWalletTransactionId(), walletTransactionId);
            if (m.getType() == Type.FEE) {
                assertNull(m.getPaymentId());
            } else {
                assertEquals(paymentId, m.getPaymentId());
            }
        }
    }

    @Test
    public void testTransfer_recipientRepositoryFails() {
        doThrow(new RuntimeException("mocked exception")).when(bankAccountRepository).save(eq(recipient));

        assertThrows(TransactionException.class, () -> service.transfer(userId, recipient, amount));
    }

    @Test
    public void testTransfer_lockNotAcquired() throws Exception {
        when(context.getBean(eq(LockRepository.class), eq(userId))).thenReturn(lockRepository);
        when(lockRepository.acquire()).thenReturn(false);

        assertThrows(TransactionException.class, () -> service.transfer(userId, recipient, amount));

        verify(lockRepository, times(1)).close();
    }

    @Test
    public void testTransfer_insufficientBalance() throws Exception {
        when(context.getBean(eq(LockRepository.class), eq(userId))).thenReturn(lockRepository);
        when(lockRepository.acquire()).thenReturn(true);
        when(walletAPIClient.getBalance(eq(userId))).thenReturn(new BigDecimal(200));

        assertThrows(TransactionException.class, () -> service.transfer(userId, recipient, amount));

        verify(lockRepository, times(1)).close();
    }

    @Test
    public void testTransfer_getBalanceFails() throws Exception {
        when(context.getBean(eq(LockRepository.class), eq(userId))).thenReturn(lockRepository);
        when(lockRepository.acquire()).thenReturn(true);
        doThrow(new WalletApiException("mocked exception")).when(walletAPIClient).getBalance(eq(userId));

        assertThrows(TransactionException.class, () -> service.transfer(userId, recipient, amount));

        verify(lockRepository, times(1)).close();
    }

    @Test
    public void testTransfer_walletTransactionFails() throws Exception {
        when(context.getBean(eq(LockRepository.class), eq(userId))).thenReturn(lockRepository);
        when(lockRepository.acquire()).thenReturn(true);
        when(walletAPIClient.getBalance(eq(userId))).thenReturn(new BigDecimal(5000));

        BigDecimal total = BigDecimal.valueOf(-1100.0);
        doThrow(new WalletApiException("mocked exception")).when(walletAPIClient).createTransaction(eq(userId), eq(total));

        assertThrows(TransactionException.class, () -> service.transfer(userId, recipient, amount));

        verify(lockRepository, times(1)).close();
    }

    @Test
    public void testTransfer_paymentProviderFails() throws Exception {
        when(context.getBean(eq(LockRepository.class), eq(userId))).thenReturn(lockRepository);
        when(lockRepository.acquire()).thenReturn(true);
        when(walletAPIClient.getBalance(eq(userId))).thenReturn(new BigDecimal(5000));

        BigDecimal total = BigDecimal.valueOf(-1100.0);
        Long walletTransactionId = 555L;
        when(walletAPIClient.createTransaction(eq(userId), eq(total))).thenReturn(walletTransactionId);

        doThrow(new PaymentProviderApiException("mocked error")).when(paymentProviderAPIClient).createPayment(eq(sourceOwner), eq(recipient), eq(amount));
        assertThrows(TransactionException.class, () -> service.transfer(userId, recipient, amount));

        verify(walletAPIClient, times(1)).createTransaction(eq(userId), eq(total));
        verify(walletAPIClient, times(1)).createTransaction(eq(userId), eq(total.negate()));

        verify(lockRepository, times(1)).close();
    }

    @Test
    public void testTransfer_paymentProviderResponseIsError() throws Exception {
        when(context.getBean(eq(LockRepository.class), eq(userId))).thenReturn(lockRepository);
        when(lockRepository.acquire()).thenReturn(true);
        when(walletAPIClient.getBalance(eq(userId))).thenReturn(new BigDecimal(5000));

        BigDecimal total = BigDecimal.valueOf(-1100.0);
        Long walletTransactionId = 555L;
        when(walletAPIClient.createTransaction(eq(userId), eq(total))).thenReturn(walletTransactionId);

        UUID paymentId = UUID.randomUUID();
        Payment response = new Payment(paymentId, new BigDecimal(3999), "error", "error", null);
        when(paymentProviderAPIClient.createPayment(eq(sourceOwner), eq(recipient), eq(amount))).thenReturn(response);

        assertThrows(TransactionException.class, () -> service.transfer(userId, recipient, amount));

        verify(walletAPIClient, times(1)).createTransaction(eq(userId), eq(total));
        verify(walletAPIClient, times(1)).createTransaction(eq(userId), eq(total.negate()));

        verify(lockRepository, times(1)).close();
    }

    @Test
    public void testTransfer_paymentProviderFailsAndLaterWalletFails() throws Exception {
        when(context.getBean(eq(LockRepository.class), eq(userId))).thenReturn(lockRepository);
        when(lockRepository.acquire()).thenReturn(true);
        when(walletAPIClient.getBalance(eq(userId))).thenReturn(new BigDecimal(5000));

        BigDecimal total = BigDecimal.valueOf(-1100.0);
        Long walletTransactionId = 555L;
        when(walletAPIClient.createTransaction(eq(userId), eq(total))).thenReturn(walletTransactionId).
                thenThrow(new WalletApiException("mocked wallet exception"));

        doThrow(new PaymentProviderApiException("mocked provider error")).when(paymentProviderAPIClient).createPayment(eq(sourceOwner), eq(recipient), eq(amount));
        assertThrows(TransactionException.class, () -> service.transfer(userId, recipient, amount));

        verify(walletAPIClient, times(1)).createTransaction(eq(userId), eq(total));
        verify(walletAPIClient, times(1)).createTransaction(eq(userId), eq(total.negate()));

        verify(lockRepository, times(1)).close();
    }

    @Test
    public void testTransfer_movementRepositoryFails() throws Exception {
        when(context.getBean(eq(LockRepository.class), eq(userId))).thenReturn(lockRepository);
        when(lockRepository.acquire()).thenReturn(true);
        when(walletAPIClient.getBalance(eq(userId))).thenReturn(new BigDecimal(5000));

        BigDecimal total = BigDecimal.valueOf(-1100.0);
        Long walletTransactionId = 555L;
        when(walletAPIClient.createTransaction(eq(userId), eq(total))).thenReturn(walletTransactionId);

        UUID paymentId = UUID.randomUUID();
        Payment response = new Payment(paymentId, new BigDecimal(3999), "ok", null, null);
        when(paymentProviderAPIClient.createPayment(eq(sourceOwner), eq(recipient), eq(amount))).thenReturn(response);

        Mockito.doThrow(new RuntimeException("mocked exception")).when(movementRepository).save(any(Transaction.class));

        assertThrows(TransactionException.class, () -> service.transfer(userId, recipient, amount));

        verify(bankAccountRepository, times(1)).save(eq(recipient));

        verify(lockRepository, times(1)).close();
    }
}
