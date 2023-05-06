package ar.com.ariel17.ontop;

import ar.com.ariel17.ontop.adapters.clients.PaymentProviderApiClientImpl;
import ar.com.ariel17.ontop.adapters.clients.WalletApiClientImpl;
import ar.com.ariel17.ontop.adapters.http.TransferController;
import ar.com.ariel17.ontop.adapters.http.entities.requests.TransferAccount;
import ar.com.ariel17.ontop.adapters.http.entities.requests.TransferRecipient;
import ar.com.ariel17.ontop.adapters.http.entities.requests.TransferRequest;
import ar.com.ariel17.ontop.adapters.http.entities.responses.TransferResponse;
import ar.com.ariel17.ontop.adapters.repositories.entities.BankAccountOwnerEntity;
import ar.com.ariel17.ontop.adapters.repositories.entities.BankAccountOwnerMapper;
import ar.com.ariel17.ontop.adapters.repositories.jpa.JpaBankAccountRepository;
import ar.com.ariel17.ontop.adapters.repositories.jpa.JpaMovementRepository;
import ar.com.ariel17.ontop.adapters.repositories.jpa.JpaPaymentRepository;
import ar.com.ariel17.ontop.core.domain.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.locks.ExpirableLockRegistry;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AppApplicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaBankAccountRepository bankAccountRepository;

    @Autowired
    private JpaPaymentRepository paymentRepository;

    @Autowired
    private JpaMovementRepository movementRepository;

    @Mock
    private Lock lock;

    @MockBean
    private ExpirableLockRegistry redisLockRegistry;

    @MockBean(name = "paymentProviderRestTemplate")
    private RestTemplate paymentProviderRestTemplate;

    @MockBean(name = "walletRestTemplate")
    private RestTemplate walletRestTemplate;

    private ObjectMapper mapper;

    private Long userId;

    private String walletBalanceResponseOK;

    private String walletTransactionResponseOK;

    private String walletTransactionResponse404;

    private String walletTransactionResponse500;

    private String providerPaymentOK;

    private String providerPayment400;

    private String providerPayment500_failed;

    private String providerPayment500_timeout;

    private Currency currency;

    @BeforeEach
    public void setUp() {
        when(redisLockRegistry.obtain(any())).thenReturn(lock);

        currency = Currency.getInstance("USD");
        mapper = new ObjectMapper();
        userId = 1000L;
        walletBalanceResponseOK = """
                {
                    "balance": 2500,
                    "user_id": 1000
                }""";
        walletTransactionResponseOK = """
                {
                    "wallet_transaction_id": 59974,
                    "amount": -2300,
                    "user_id": 1000
                }""";
        walletTransactionResponse404 = """
                {
                    "code": "INVALID_USER",
                    "message": "user not found"
                }""";
        walletTransactionResponse500 = """
                {
                    "code": "GENERIC_ERROR",
                    "message": "something bad happened"
                }""";
        providerPaymentOK = """
                {
                    "requestInfo": {
                        "status": "Processing"
                    },
                    "paymentInfo": {
                        "amount": 1000,
                        "id": "70cfe468-91b9-4e04-8910-5e8257dfadfa"
                    }
                }""";
        providerPayment400 = """
                {
                    "error": "body is invalid, check postman collection example"
                }""";
        providerPayment500_failed = """
                {
                    "requestInfo": {
                        "status": "Failed",
                        "error": "bank rejected payment"
                    },
                    "paymentInfo": {
                        "amount": 1000,
                        "id": "7633f4c9-51e4-4b62-97b0-51156966f1d7"
                    }
                }""";
        providerPayment500_timeout = """
                {
                    "requestInfo": {
                        "status": "Failed",
                        "error": "timeout"
                    },
                    "paymentInfo": {
                        "amount": 1000,
                        "id": "3656ee8e-caff-4f2b-9ed3-2ba9fb938fb7"
                    }
                }""";
    }

    @Test
    public void testTransfer_ok_fullRecipient() throws Exception {
        when(lock.tryLock()).thenReturn(true);

        String balanceUri = String.format(WalletApiClientImpl.BALANCE_URI_TEMPLATE, userId);
        ResponseEntity<String> response = ResponseEntity.
                status(HttpStatus.OK).
                body(walletBalanceResponseOK);
        when(walletRestTemplate.exchange(eq(balanceUri), eq(HttpMethod.GET), any(), eq(String.class))).
                thenReturn(response);

        response = ResponseEntity.
                status(HttpStatus.OK).
                body(walletTransactionResponseOK);
        when(walletRestTemplate.exchange(eq(WalletApiClientImpl.TRANSACTIONS_URI), eq(HttpMethod.POST), any(), eq(String.class))).
                thenReturn(response);

        response = ResponseEntity.
                status(HttpStatus.OK).
                body(providerPaymentOK);
        when(paymentProviderRestTemplate.exchange(eq(PaymentProviderApiClientImpl.PAYMENT_URI), eq(HttpMethod.POST), any(), eq(String.class))).
                thenReturn(response);

        BigDecimal amount = new BigDecimal(1000);
        TransferAccount account = TransferAccount.builder().
                routingNumber("012345689").
                accountNumber("012345789").
                currency(currency).
                build();
        TransferRecipient recipient = TransferRecipient.builder().
                account(account).
                idNumber("123ABC").
                firstName("John").
                lastName("Snow").
                build();
        TransferRequest request = TransferRequest.builder().
                userId(userId).
                recipient(recipient).
                currency(currency).
                amount(amount).
                build();

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(TransferController.TRANSFERS_URI).
                        content(mapper.writeValueAsString(request)).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();

        String responseBody = result.getResponse().getContentAsString();

        TransferResponse transferResponse = mapper.readValue(responseBody, TransferResponse.class);
        assertEquals(userId, transferResponse.getUserId());
        assertEquals(PaymentStatus.PROCESSING, transferResponse.getStatus());
        assertEquals(Operation.WITHDRAW, transferResponse.getOperation());
        assertEquals(2L, transferResponse.getRecipientId());
        assertEquals(2, transferResponse.getMovements().size());

        AtomicInteger totalTransfers = new AtomicInteger();
        AtomicInteger totalFees = new AtomicInteger();
        transferResponse.getMovements().forEach(m -> {
            assertEquals(m.getCurrency(), currency);
            if (m.getType() == MovementType.TRANSFER) {
                totalTransfers.getAndIncrement();
                assertEquals(amount.negate(), m.getAmount());
            } else {
                totalFees.getAndIncrement();
                assertEquals(new BigDecimal("-100.0"), m.getAmount());
            }
        });
        assertEquals(1, totalTransfers.get());
        assertEquals(1, totalFees.get());
    }

    @Test
    public void testTransfer_ok_recipientId() throws Exception {
        BankAccount account = BankAccount.builder().
                routing("0987654321").
                account("0987654321").
                currency(currency).
                build();
        BankAccountOwner owner = BankAccountOwner.builder().
                userId(userId).
                bankAccount(account).
                idNumber("ABC123").
                firstName("John").
                lastName("Doe").
                build();

        BankAccountOwnerEntity entity = BankAccountOwnerMapper.INSTANCE.
                bankAccountOwnerToBankAccountOwnerEntity(owner);
        entity = bankAccountRepository.save(entity);

        when(lock.tryLock()).thenReturn(true);

        String balanceUri = String.format(WalletApiClientImpl.BALANCE_URI_TEMPLATE, userId);
        ResponseEntity<String> response = ResponseEntity.
                status(HttpStatus.OK).
                body(walletBalanceResponseOK);
        when(walletRestTemplate.exchange(eq(balanceUri), eq(HttpMethod.GET), any(), eq(String.class))).
                thenReturn(response);

        response = ResponseEntity.
                status(HttpStatus.OK).
                body(walletTransactionResponseOK);
        when(walletRestTemplate.exchange(eq(WalletApiClientImpl.TRANSACTIONS_URI), eq(HttpMethod.POST), any(), eq(String.class))).
                thenReturn(response);

        response = ResponseEntity.
                status(HttpStatus.OK).
                body(providerPaymentOK);
        when(paymentProviderRestTemplate.exchange(eq(PaymentProviderApiClientImpl.PAYMENT_URI), eq(HttpMethod.POST), any(), eq(String.class))).
                thenReturn(response);

        BigDecimal amount = new BigDecimal(1000);
        TransferRequest request = TransferRequest.builder().
                userId(userId).
                recipientId(entity.getId()).
                currency(currency).
                amount(amount).
                build();

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(TransferController.TRANSFERS_URI).
                        content(mapper.writeValueAsString(request)).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();

        String responseBody = result.getResponse().getContentAsString();

        TransferResponse transferResponse = mapper.readValue(responseBody, TransferResponse.class);
        assertEquals(userId, transferResponse.getUserId());
        assertEquals(PaymentStatus.PROCESSING, transferResponse.getStatus());
        assertEquals(Operation.WITHDRAW, transferResponse.getOperation());
        assertEquals(entity.getId(), transferResponse.getRecipientId());
        assertEquals(2, transferResponse.getMovements().size());

        AtomicInteger totalTransfers = new AtomicInteger();
        AtomicInteger totalFees = new AtomicInteger();
        transferResponse.getMovements().forEach(m -> {
            assertEquals(m.getCurrency(), currency);
            if (m.getType() == MovementType.TRANSFER) {
                totalTransfers.getAndIncrement();
                assertEquals(amount.negate(), m.getAmount());
            } else {
                totalFees.getAndIncrement();
                assertEquals(new BigDecimal("-100.0"), m.getAmount());
            }
        });
        assertEquals(1, totalTransfers.get());
        assertEquals(1, totalFees.get());
    }

    @Test
    public void testTransfer_ok_noCurrency() throws Exception {
        when(lock.tryLock()).thenReturn(true);

        String balanceUri = String.format(WalletApiClientImpl.BALANCE_URI_TEMPLATE, userId);
        ResponseEntity<String> response = ResponseEntity.
                status(HttpStatus.OK).
                body(walletBalanceResponseOK);
        when(walletRestTemplate.exchange(eq(balanceUri), eq(HttpMethod.GET), any(), eq(String.class))).
                thenReturn(response);

        response = ResponseEntity.
                status(HttpStatus.OK).
                body(walletTransactionResponseOK);
        when(walletRestTemplate.exchange(eq(WalletApiClientImpl.TRANSACTIONS_URI), eq(HttpMethod.POST), any(), eq(String.class))).
                thenReturn(response);

        response = ResponseEntity.
                status(HttpStatus.OK).
                body(providerPaymentOK);
        when(paymentProviderRestTemplate.exchange(eq(PaymentProviderApiClientImpl.PAYMENT_URI), eq(HttpMethod.POST), any(), eq(String.class))).
                thenReturn(response);

        BigDecimal amount = new BigDecimal(1000);
        TransferAccount account = TransferAccount.builder().
                routingNumber("012345689").
                accountNumber("012345789").
                build();
        TransferRecipient recipient = TransferRecipient.builder().
                account(account).
                idNumber("123ABC").
                firstName("John").
                lastName("Snow").
                build();
        TransferRequest request = TransferRequest.builder().
                userId(userId).
                recipient(recipient).
                amount(amount).
                build();

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(TransferController.TRANSFERS_URI).
                        content(mapper.writeValueAsString(request)).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();

        String responseBody = result.getResponse().getContentAsString();

        TransferResponse transferResponse = mapper.readValue(responseBody, TransferResponse.class);
        assertEquals(userId, transferResponse.getUserId());
        assertEquals(PaymentStatus.PROCESSING, transferResponse.getStatus());
        assertEquals(Operation.WITHDRAW, transferResponse.getOperation());
        assertEquals(2L, transferResponse.getRecipientId());
        assertEquals(2, transferResponse.getMovements().size());

        AtomicInteger totalTransfers = new AtomicInteger();
        AtomicInteger totalFees = new AtomicInteger();
        transferResponse.getMovements().forEach(m -> {
            assertEquals(m.getCurrency(), currency);
            if (m.getType() == MovementType.TRANSFER) {
                totalTransfers.getAndIncrement();
                assertEquals(amount.negate(), m.getAmount());
            } else {
                totalFees.getAndIncrement();
                assertEquals(new BigDecimal("-100.0"), m.getAmount());
            }
        });
        assertEquals(1, totalTransfers.get());
        assertEquals(1, totalFees.get());
    }

    @Test
    public void testTransfer_failed_multipleRecipients() throws Exception {
        BigDecimal amount = new BigDecimal(1000);
        TransferAccount account = TransferAccount.builder().
                routingNumber("012345689").
                accountNumber("012345789").
                currency(currency).
                build();
        TransferRecipient recipient = TransferRecipient.builder().
                account(account).
                idNumber("123ABC").
                firstName("John").
                lastName("Snow").
                build();
        TransferRequest request = TransferRequest.builder().
                userId(userId).
                recipientId(10L).
                recipient(recipient).
                currency(currency).
                amount(amount).
                build();

        mockMvc.perform(
                MockMvcRequestBuilders.post(TransferController.TRANSFERS_URI).
                        content(mapper.writeValueAsString(request)).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void testTransfer_failed_missingAccountFields() throws Exception {
        BigDecimal amount = new BigDecimal(1000);
        TransferAccount account = TransferAccount.builder().
                currency(currency).
                build();
        TransferRecipient recipient = TransferRecipient.builder().
                account(account).
                idNumber("123ABC").
                firstName("John").
                lastName("Snow").
                build();
        TransferRequest request = TransferRequest.builder().
                userId(userId).
                recipient(recipient).
                currency(currency).
                amount(amount).
                build();

        mockMvc.perform(
                MockMvcRequestBuilders.post(TransferController.TRANSFERS_URI).
                        content(mapper.writeValueAsString(request)).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void testTransfer_failed_invalidAccountFields() throws Exception {
        BigDecimal amount = new BigDecimal(1000);
        TransferAccount account = TransferAccount.builder().
                routingNumber("0123").
                accountNumber("0123").
                currency(currency).
                build();
        TransferRecipient recipient = TransferRecipient.builder().
                account(account).
                idNumber("123ABC").
                firstName("John").
                lastName("Snow").
                build();
        TransferRequest request = TransferRequest.builder().
                userId(userId).
                recipientId(10L).
                recipient(recipient).
                currency(currency).
                amount(amount).
                build();

        mockMvc.perform(
                MockMvcRequestBuilders.post(TransferController.TRANSFERS_URI).
                        content(mapper.writeValueAsString(request)).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void testTransfer_failed_missingUserId() throws Exception {
        BigDecimal amount = new BigDecimal(1000);
        TransferAccount account = TransferAccount.builder().
                routingNumber("0123456789").
                accountNumber("0123456789").
                currency(currency).
                build();
        TransferRecipient recipient = TransferRecipient.builder().
                account(account).
                idNumber("123ABC").
                firstName("John").
                lastName("Snow").
                build();
        TransferRequest request = TransferRequest.builder().
                recipientId(10L).
                recipient(recipient).
                currency(currency).
                amount(amount).
                build();

        mockMvc.perform(
                MockMvcRequestBuilders.post(TransferController.TRANSFERS_URI).
                        content(mapper.writeValueAsString(request)).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void testTransfer_failed_missingAmount() throws Exception {
        TransferAccount account = TransferAccount.builder().
                routingNumber("0123456789").
                accountNumber("0123456789").
                currency(currency).
                build();
        TransferRecipient recipient = TransferRecipient.builder().
                account(account).
                idNumber("123ABC").
                firstName("John").
                lastName("Snow").
                build();
        TransferRequest request = TransferRequest.builder().
                userId(userId).
                recipientId(10L).
                recipient(recipient).
                currency(currency).
                build();

        mockMvc.perform(
                MockMvcRequestBuilders.post(TransferController.TRANSFERS_URI).
                        content(mapper.writeValueAsString(request)).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void testTransfer_failed_missingRecipientData() throws Exception {
        BigDecimal amount = new BigDecimal(1000);
        TransferAccount account = TransferAccount.builder().
                routingNumber("0123456789").
                accountNumber("0123456789").
                currency(currency).
                build();
        TransferRecipient recipient = TransferRecipient.builder().
                account(account).
                build();
        TransferRequest request = TransferRequest.builder().
                userId(userId).
                recipientId(10L).
                recipient(recipient).
                currency(currency).
                amount(amount).
                build();

        mockMvc.perform(
                MockMvcRequestBuilders.post(TransferController.TRANSFERS_URI).
                        content(mapper.writeValueAsString(request)).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void testTransfer_failed_walletTransaction404() throws Exception {
        when(lock.tryLock()).thenReturn(true);

        String balanceUri = String.format(WalletApiClientImpl.BALANCE_URI_TEMPLATE, userId);
        ResponseEntity<String> response = ResponseEntity.
                status(HttpStatus.OK).
                body(walletBalanceResponseOK);
        when(walletRestTemplate.exchange(eq(balanceUri), eq(HttpMethod.GET), any(), eq(String.class))).
                thenReturn(response);

        doThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, walletTransactionResponse404)).
                when(walletRestTemplate).exchange(eq(WalletApiClientImpl.TRANSACTIONS_URI), eq(HttpMethod.POST), any(), eq(String.class));

        BigDecimal amount = new BigDecimal(1000);
        TransferAccount account = TransferAccount.builder().
                routingNumber("012345689").
                accountNumber("012345789").
                build();
        TransferRecipient recipient = TransferRecipient.builder().
                account(account).
                idNumber("123ABC").
                firstName("John").
                lastName("Snow").
                build();
        TransferRequest request = TransferRequest.builder().
                userId(userId).
                recipient(recipient).
                amount(amount).
                build();

        mockMvc.perform(
                MockMvcRequestBuilders.post(TransferController.TRANSFERS_URI).
                        content(mapper.writeValueAsString(request)).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    public void testTransfer_failed_walletTransaction500() throws Exception {
        when(lock.tryLock()).thenReturn(true);

        String balanceUri = String.format(WalletApiClientImpl.BALANCE_URI_TEMPLATE, userId);
        ResponseEntity<String> response = ResponseEntity.
                status(HttpStatus.OK).
                body(walletBalanceResponseOK);
        when(walletRestTemplate.exchange(eq(balanceUri), eq(HttpMethod.GET), any(), eq(String.class))).
                thenReturn(response);

        doThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, walletTransactionResponse500)).
                when(walletRestTemplate).exchange(eq(WalletApiClientImpl.TRANSACTIONS_URI), eq(HttpMethod.POST), any(), eq(String.class));

        BigDecimal amount = new BigDecimal(1000);
        TransferAccount account = TransferAccount.builder().
                routingNumber("012345689").
                accountNumber("012345789").
                build();
        TransferRecipient recipient = TransferRecipient.builder().
                account(account).
                idNumber("123ABC").
                firstName("John").
                lastName("Snow").
                build();
        TransferRequest request = TransferRequest.builder().
                userId(userId).
                recipient(recipient).
                amount(amount).
                build();

        mockMvc.perform(
                MockMvcRequestBuilders.post(TransferController.TRANSFERS_URI).
                        content(mapper.writeValueAsString(request)).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isInternalServerError());
    }

    @Test
    public void testTransfer_failed_walletBalance_unexpected() throws Exception {
        when(lock.tryLock()).thenReturn(true);

        String balanceUri = String.format(WalletApiClientImpl.BALANCE_URI_TEMPLATE, userId);
        doThrow(new RuntimeException("unexpected")).when(walletRestTemplate).
                exchange(eq(balanceUri), eq(HttpMethod.GET), any(), eq(String.class));

        BigDecimal amount = new BigDecimal(1000);
        TransferAccount account = TransferAccount.builder().
                routingNumber("012345689").
                accountNumber("012345789").
                build();
        TransferRecipient recipient = TransferRecipient.builder().
                account(account).
                idNumber("123ABC").
                firstName("John").
                lastName("Snow").
                build();
        TransferRequest request = TransferRequest.builder().
                userId(userId).
                recipient(recipient).
                amount(amount).
                build();

        mockMvc.perform(
                MockMvcRequestBuilders.post(TransferController.TRANSFERS_URI).
                        content(mapper.writeValueAsString(request)).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isInternalServerError());
    }

    @Test
    public void testTransfer_failed_walletTransaction_unexpected() throws Exception {
        when(lock.tryLock()).thenReturn(true);

        String balanceUri = String.format(WalletApiClientImpl.BALANCE_URI_TEMPLATE, userId);
        ResponseEntity<String> response = ResponseEntity.
                status(HttpStatus.OK).
                body(walletBalanceResponseOK);
        when(walletRestTemplate.exchange(eq(balanceUri), eq(HttpMethod.GET), any(), eq(String.class))).
                thenReturn(response);

        doThrow(new RuntimeException("unexpected")).when(walletRestTemplate).
                exchange(eq(WalletApiClientImpl.TRANSACTIONS_URI), eq(HttpMethod.POST), any(), eq(String.class));

        BigDecimal amount = new BigDecimal(1000);
        TransferAccount account = TransferAccount.builder().
                routingNumber("012345689").
                accountNumber("012345789").
                build();
        TransferRecipient recipient = TransferRecipient.builder().
                account(account).
                idNumber("123ABC").
                firstName("John").
                lastName("Snow").
                build();
        TransferRequest request = TransferRequest.builder().
                userId(userId).
                recipient(recipient).
                amount(amount).
                build();

        mockMvc.perform(
                MockMvcRequestBuilders.post(TransferController.TRANSFERS_URI).
                        content(mapper.writeValueAsString(request)).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isInternalServerError());
    }

    @Test
    public void testTransfer_failed_providerResponse400() throws Exception {
        when(lock.tryLock()).thenReturn(true);

        String balanceUri = String.format(WalletApiClientImpl.BALANCE_URI_TEMPLATE, userId);
        ResponseEntity<String> response = ResponseEntity.
                status(HttpStatus.OK).
                body(walletBalanceResponseOK);
        when(walletRestTemplate.exchange(eq(balanceUri), eq(HttpMethod.GET), any(), eq(String.class))).
                thenReturn(response);

        response = ResponseEntity.
                status(HttpStatus.OK).
                body(walletTransactionResponseOK);
        when(walletRestTemplate.exchange(eq(WalletApiClientImpl.TRANSACTIONS_URI), eq(HttpMethod.POST), any(), eq(String.class))).
                thenReturn(response);

        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, providerPayment400)).
                when(paymentProviderRestTemplate).exchange(eq(PaymentProviderApiClientImpl.PAYMENT_URI), eq(HttpMethod.POST), any(), eq(String.class));

        BigDecimal amount = new BigDecimal(1000);
        TransferAccount account = TransferAccount.builder().
                routingNumber("012345689").
                accountNumber("012345789").
                currency(currency).
                build();
        TransferRecipient recipient = TransferRecipient.builder().
                account(account).
                idNumber("123ABC").
                firstName("John").
                lastName("Snow").
                build();
        TransferRequest request = TransferRequest.builder().
                userId(userId).
                recipient(recipient).
                currency(currency).
                amount(amount).
                build();

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(TransferController.TRANSFERS_URI).
                        content(mapper.writeValueAsString(request)).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();

        String responseBody = result.getResponse().getContentAsString();

        TransferResponse transferResponse = mapper.readValue(responseBody, TransferResponse.class);
        assertEquals(userId, transferResponse.getUserId());
        assertEquals(PaymentStatus.FAILED, transferResponse.getStatus());
        assertEquals(Operation.REVERT, transferResponse.getOperation());
        assertEquals(2L, transferResponse.getRecipientId());
        assertEquals(4, transferResponse.getMovements().size());

        AtomicInteger totalTransfers = new AtomicInteger();
        AtomicInteger totalFees = new AtomicInteger();
        transferResponse.getMovements().forEach(m -> {
            assertEquals(m.getCurrency(), currency);
            if (m.getType() == MovementType.TRANSFER) {
                totalTransfers.getAndIncrement();
                assertEquals(amount.abs(), m.getAmount().abs());
            } else {
                totalFees.getAndIncrement();
                assertEquals(new BigDecimal("100.0"), m.getAmount().abs());
            }
        });
        assertEquals(2, totalTransfers.get());
        assertEquals(2, totalFees.get());
    }

    @Test
    public void testTransfer_failed_providerResponse500Failed() throws Exception {
        when(lock.tryLock()).thenReturn(true);

        String balanceUri = String.format(WalletApiClientImpl.BALANCE_URI_TEMPLATE, userId);
        ResponseEntity<String> response = ResponseEntity.
                status(HttpStatus.OK).
                body(walletBalanceResponseOK);
        when(walletRestTemplate.exchange(eq(balanceUri), eq(HttpMethod.GET), any(), eq(String.class))).
                thenReturn(response);

        response = ResponseEntity.
                status(HttpStatus.OK).
                body(walletTransactionResponseOK);
        when(walletRestTemplate.exchange(eq(WalletApiClientImpl.TRANSACTIONS_URI), eq(HttpMethod.POST), any(), eq(String.class))).
                thenReturn(response);

        doThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, providerPayment500_failed)).
                when(paymentProviderRestTemplate).exchange(eq(PaymentProviderApiClientImpl.PAYMENT_URI), eq(HttpMethod.POST), any(), eq(String.class));

        BigDecimal amount = new BigDecimal(1000);
        TransferAccount account = TransferAccount.builder().
                routingNumber("012345689").
                accountNumber("012345789").
                currency(currency).
                build();
        TransferRecipient recipient = TransferRecipient.builder().
                account(account).
                idNumber("123ABC").
                firstName("John").
                lastName("Snow").
                build();
        TransferRequest request = TransferRequest.builder().
                userId(userId).
                recipient(recipient).
                currency(currency).
                amount(amount).
                build();

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(TransferController.TRANSFERS_URI).
                        content(mapper.writeValueAsString(request)).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();

        String responseBody = result.getResponse().getContentAsString();

        TransferResponse transferResponse = mapper.readValue(responseBody, TransferResponse.class);
        assertEquals(userId, transferResponse.getUserId());
        assertEquals(PaymentStatus.FAILED, transferResponse.getStatus());
        assertEquals(Operation.REVERT, transferResponse.getOperation());
        assertEquals(2L, transferResponse.getRecipientId());
        assertEquals(4, transferResponse.getMovements().size());

        AtomicInteger totalTransfers = new AtomicInteger();
        AtomicInteger totalFees = new AtomicInteger();
        transferResponse.getMovements().forEach(m -> {
            assertEquals(m.getCurrency(), currency);
            if (m.getType() == MovementType.TRANSFER) {
                totalTransfers.getAndIncrement();
                assertEquals(amount.abs(), m.getAmount().abs());
            } else {
                totalFees.getAndIncrement();
                assertEquals(new BigDecimal("100.0"), m.getAmount().abs());
            }
        });
        assertEquals(2, totalTransfers.get());
        assertEquals(2, totalFees.get());
    }

    @Test
    public void testTransfer_failed_providerResponse500Timeout() throws Exception {
        when(lock.tryLock()).thenReturn(true);

        String balanceUri = String.format(WalletApiClientImpl.BALANCE_URI_TEMPLATE, userId);
        ResponseEntity<String> response = ResponseEntity.
                status(HttpStatus.OK).
                body(walletBalanceResponseOK);
        when(walletRestTemplate.exchange(eq(balanceUri), eq(HttpMethod.GET), any(), eq(String.class))).
                thenReturn(response);

        response = ResponseEntity.
                status(HttpStatus.OK).
                body(walletTransactionResponseOK);
        when(walletRestTemplate.exchange(eq(WalletApiClientImpl.TRANSACTIONS_URI), eq(HttpMethod.POST), any(), eq(String.class))).
                thenReturn(response);

        doThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, providerPayment500_timeout)).
                when(paymentProviderRestTemplate).exchange(eq(PaymentProviderApiClientImpl.PAYMENT_URI), eq(HttpMethod.POST), any(), eq(String.class));

        BigDecimal amount = new BigDecimal(1000);
        TransferAccount account = TransferAccount.builder().
                routingNumber("012345689").
                accountNumber("012345789").
                currency(currency).
                build();
        TransferRecipient recipient = TransferRecipient.builder().
                account(account).
                idNumber("123ABC").
                firstName("John").
                lastName("Snow").
                build();
        TransferRequest request = TransferRequest.builder().
                userId(userId).
                recipient(recipient).
                currency(currency).
                amount(amount).
                build();

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(TransferController.TRANSFERS_URI).
                        content(mapper.writeValueAsString(request)).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();

        String responseBody = result.getResponse().getContentAsString();

        TransferResponse transferResponse = mapper.readValue(responseBody, TransferResponse.class);
        assertEquals(userId, transferResponse.getUserId());
        assertEquals(PaymentStatus.FAILED, transferResponse.getStatus());
        assertEquals(Operation.REVERT, transferResponse.getOperation());
        assertEquals(2L, transferResponse.getRecipientId());
        assertEquals(4, transferResponse.getMovements().size());

        AtomicInteger totalTransfers = new AtomicInteger();
        AtomicInteger totalFees = new AtomicInteger();
        transferResponse.getMovements().forEach(m -> {
            assertEquals(m.getCurrency(), currency);
            if (m.getType() == MovementType.TRANSFER) {
                totalTransfers.getAndIncrement();
                assertEquals(amount.abs(), m.getAmount().abs());
            } else {
                totalFees.getAndIncrement();
                assertEquals(new BigDecimal("100.0"), m.getAmount().abs());
            }
        });
        assertEquals(2, totalTransfers.get());
        assertEquals(2, totalFees.get());
    }

    @Test
    public void testTransfer_failed_unexpected() throws Exception {
        when(lock.tryLock()).thenReturn(true);

        String balanceUri = String.format(WalletApiClientImpl.BALANCE_URI_TEMPLATE, userId);
        ResponseEntity<String> response = ResponseEntity.
                status(HttpStatus.OK).
                body(walletBalanceResponseOK);
        when(walletRestTemplate.exchange(eq(balanceUri), eq(HttpMethod.GET), any(), eq(String.class))).
                thenReturn(response);

        response = ResponseEntity.
                status(HttpStatus.OK).
                body(walletTransactionResponseOK);
        when(walletRestTemplate.exchange(eq(WalletApiClientImpl.TRANSACTIONS_URI), eq(HttpMethod.POST), any(), eq(String.class))).
                thenReturn(response);

        doThrow(new RuntimeException("unexpected")).when(paymentProviderRestTemplate).
                exchange(eq(PaymentProviderApiClientImpl.PAYMENT_URI), eq(HttpMethod.POST), any(), eq(String.class));

        BigDecimal amount = new BigDecimal(1000);
        TransferAccount account = TransferAccount.builder().
                routingNumber("012345689").
                accountNumber("012345789").
                currency(currency).
                build();
        TransferRecipient recipient = TransferRecipient.builder().
                account(account).
                idNumber("123ABC").
                firstName("John").
                lastName("Snow").
                build();
        TransferRequest request = TransferRequest.builder().
                userId(userId).
                recipient(recipient).
                currency(currency).
                amount(amount).
                build();

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post(TransferController.TRANSFERS_URI).
                        content(mapper.writeValueAsString(request)).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();

        String responseBody = result.getResponse().getContentAsString();

        TransferResponse transferResponse = mapper.readValue(responseBody, TransferResponse.class);
        assertEquals(userId, transferResponse.getUserId());
        assertEquals(PaymentStatus.FAILED, transferResponse.getStatus());
        assertEquals(Operation.REVERT, transferResponse.getOperation());
        assertEquals(2L, transferResponse.getRecipientId());
        assertEquals(4, transferResponse.getMovements().size());

        AtomicInteger totalTransfers = new AtomicInteger();
        AtomicInteger totalFees = new AtomicInteger();
        transferResponse.getMovements().forEach(m -> {
            assertEquals(m.getCurrency(), currency);
            if (m.getType() == MovementType.TRANSFER) {
                totalTransfers.getAndIncrement();
                assertEquals(amount.abs(), m.getAmount().abs());
            } else {
                totalFees.getAndIncrement();
                assertEquals(new BigDecimal("100.0"), m.getAmount().abs());
            }
        });
        assertEquals(2, totalTransfers.get());
        assertEquals(2, totalFees.get());
    }

    @Test
    public void testTransfer_failedLock() throws Exception {
        when(lock.tryLock()).thenReturn(false);

        BigDecimal amount = new BigDecimal(1000);
        TransferAccount account = TransferAccount.builder().
                routingNumber("012345689").
                accountNumber("012345789").
                build();
        TransferRecipient recipient = TransferRecipient.builder().
                account(account).
                idNumber("123ABC").
                firstName("John").
                lastName("Snow").
                build();
        TransferRequest request = TransferRequest.builder().
                userId(userId).
                recipient(recipient).
                amount(amount).
                build();

        mockMvc.perform(
                MockMvcRequestBuilders.post(TransferController.TRANSFERS_URI).
                        content(mapper.writeValueAsString(request)).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isInternalServerError());
    }
}
