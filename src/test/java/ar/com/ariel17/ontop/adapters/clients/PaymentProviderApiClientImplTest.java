package ar.com.ariel17.ontop.adapters.clients;

import ar.com.ariel17.ontop.core.clients.PaymentProviderApiException;
import ar.com.ariel17.ontop.core.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentProviderApiClientImplTest {

    @Mock
    private RestTemplate restTemplate;

    private PaymentProviderApiClientImpl client;

    private String bodyOk;

    private String body4xxError;

    private String body5xxErrorTimeout;

    private String body5xxErrorRejection;

    private BankAccountOwner onTopAccount;

    private BankAccountOwner externalAccount;

    private String requestBody;

    private BigDecimal amount;

    @BeforeEach
    public void setUp() {
        client = spy(new PaymentProviderApiClientImpl(restTemplate));
        bodyOk = """
                {
                    "requestInfo": {
                        "status": "Processing"
                    },
                    "paymentInfo": {
                        "amount": 1000,
                        "id": "70cfe468-91b9-4e04-8910-5e8257dfadfa"
                    }
                }""";
        body4xxError = """
                {
                    "error": "body is invalid, check postman collection example"
                }""";
        body5xxErrorTimeout = """
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
        body5xxErrorRejection = """
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

        BankAccount account = BankAccount.builder().
                routing("0123456789").
                account("012345678").
                type(BankAccountType.COMPANY).
                currency(Currency.getInstance("USD")).build();
        onTopAccount = BankAccountOwner.builder().
                userId(10L).
                bankAccount(account).
                firstName("ONTOP INC").build();

        account = BankAccount.builder().
                routing("9876543210").
                account("876543210").
                type(BankAccountType.COMPANY).
                currency(Currency.getInstance("USD")).build();
        externalAccount = BankAccountOwner.builder().
                userId(10L).
                bankAccount(account).
                idNumber("123ABC").
                firstName("John").
                lastName("Snow").build();

        requestBody = "{\"source\":{\"type\":\"COMPANY\",\"sourceInformation\":{\"name\":\"ONTOP INC\"},\"account\":{\"accountNumber\":\"012345678\",\"currency\":\"USD\",\"routingNumber\":\"0123456789\"}},\"destination\":{\"name\":\"John Snow\",\"account\":{\"accountNumber\":\"876543210\",\"currency\":\"USD\",\"routingNumber\":\"9876543210\"}},\"amount\":2500}";
        amount = new BigDecimal(2500);
    }

    @Test
    public void testCreatePayment_ok() throws PaymentProviderApiException {
        ResponseEntity<String> response = new ResponseEntity<>(bodyOk, HttpStatus.OK);
        when(restTemplate.exchange(eq(PaymentProviderApiClientImpl.PAYMENT_URI), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(response);

        Payment payment = client.createPayment(onTopAccount, externalAccount, amount);
        verify(client, times(1)).post(eq(PaymentProviderApiClientImpl.PAYMENT_URI), eq(requestBody));

        assertEquals(PaymentStatus.PROCESSING, payment.getStatus());
        assertEquals(new BigDecimal(1000), payment.getAmount());
        assertNotNull(payment.getId());
    }

    @Test
    public void testCreatePayment_4xxError() {
        ResponseEntity<String> response = new ResponseEntity<>(body4xxError, HttpStatus.BAD_REQUEST);
        when(restTemplate.exchange(eq(PaymentProviderApiClientImpl.PAYMENT_URI), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(response);

        assertThrows(PaymentProviderApiException.class, () -> client.createPayment(onTopAccount, externalAccount, amount));
        verify(client, times(1)).post(eq(PaymentProviderApiClientImpl.PAYMENT_URI), eq(requestBody));
    }

    @Test
    public void testCreatePayment_5xxErrorTimeout() throws PaymentProviderApiException {
        ResponseEntity<String> response = new ResponseEntity<>(body5xxErrorTimeout, HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.exchange(eq(PaymentProviderApiClientImpl.PAYMENT_URI), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(response);

        Payment payment = client.createPayment(onTopAccount, externalAccount, amount);
        verify(client, times(1)).post(eq(PaymentProviderApiClientImpl.PAYMENT_URI), eq(requestBody));
        assertTrue(payment.isError());
    }

    @Test
    public void testCreatePayment_exchangeException() throws PaymentProviderApiException {
        ResponseEntity<String> response = new ResponseEntity<>(body5xxErrorTimeout, HttpStatus.INTERNAL_SERVER_ERROR);
        doThrow(new RestClientException("mocked exception")).when(restTemplate).exchange(eq(PaymentProviderApiClientImpl.PAYMENT_URI), eq(HttpMethod.POST), any(), eq(String.class));

        assertThrows(PaymentProviderApiException.class, () -> client.createPayment(onTopAccount, externalAccount, amount));
        verify(client, times(1)).post(eq(PaymentProviderApiClientImpl.PAYMENT_URI), eq(requestBody));
    }

    @Test
    public void testCreatePayment_2xxWithInvalidBody() throws PaymentProviderApiException {
        ResponseEntity<String> response = new ResponseEntity<>("{\"invalid_body", HttpStatus.OK);
        when(restTemplate.exchange(eq(PaymentProviderApiClientImpl.PAYMENT_URI), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(response);

        assertThrows(PaymentProviderApiException.class, () -> client.createPayment(onTopAccount, externalAccount, amount));
        verify(client, times(1)).post(eq(PaymentProviderApiClientImpl.PAYMENT_URI), eq(requestBody));
    }

    @Test
    public void testCreatePayment_5xxErrorRejection() throws PaymentProviderApiException {
        ResponseEntity<String> response = new ResponseEntity<>(body5xxErrorRejection, HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.exchange(eq(PaymentProviderApiClientImpl.PAYMENT_URI), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(response);

        Payment payment = client.createPayment(onTopAccount, externalAccount, amount);
        verify(client, times(1)).post(eq(PaymentProviderApiClientImpl.PAYMENT_URI), eq(requestBody));
        assertTrue(payment.isError());
    }
}