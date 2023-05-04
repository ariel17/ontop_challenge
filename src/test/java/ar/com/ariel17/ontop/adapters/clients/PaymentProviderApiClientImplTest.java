package ar.com.ariel17.ontop.adapters.clients;

import ar.com.ariel17.ontop.core.clients.PaymentProviderApiException;
import ar.com.ariel17.ontop.core.domain.BankAccount;
import ar.com.ariel17.ontop.core.domain.BankAccountOwner;
import ar.com.ariel17.ontop.core.domain.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

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

    private BankAccountOwner from;

    private BankAccountOwner to;

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

        BankAccount account1 = new BankAccount(1111L, 1111L, Currency.getInstance("USD"));
        BankAccount account2 = new BankAccount(2222L, 2222L, Currency.getInstance("USD"));
        from = new BankAccountOwner(1L, 99L, account1, "123ABC", "John", "Doe", new Date());
        to = new BankAccountOwner(2L, 100L, account2, "ABC123", "John", "Snow", new Date());
        requestBody = "{\"source\":{\"type\":\"COMPANY\",\"sourceInformation\":{\"name\":\"John Doe\"},\"account\":{\"accountNumber\":\"1111\",\"currency\":\"USD\",\"routingNumber\":\"1111\"}},\"destination\":{\"name\":\"John Snow\",\"account\":{\"accountNumber\":\"2222\",\"currency\":\"USD\",\"routingNumber\":\"2222\"}},\"amount\":2500}";
        amount = new BigDecimal(2500);
    }

    @Test
    public void testCreatePayment_ok() throws PaymentProviderApiException {
        ResponseEntity<String> response = new ResponseEntity<>(bodyOk, HttpStatus.OK);
        when(restTemplate.exchange(eq(PaymentProviderApiClientImpl.PAYMENT_URI), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(response);

        Payment payment = client.createPayment(from, to, amount);
        verify(client, times(1)).post(eq(PaymentProviderApiClientImpl.PAYMENT_URI), eq(requestBody));

        assertEquals("Processing", payment.getStatus());
        assertEquals(new BigDecimal(1000), payment.getAmount());
        assertNotNull(payment.getId());
    }

    @Test
    public void testGetBalance_4xxError() {
        ResponseEntity<String> response = new ResponseEntity<>(body4xxError, HttpStatus.BAD_REQUEST);
        when(restTemplate.exchange(eq(PaymentProviderApiClientImpl.PAYMENT_URI), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(response);

        assertThrows(PaymentProviderApiException.class, () -> client.createPayment(from, to, amount));
        verify(client, times(1)).post(eq(PaymentProviderApiClientImpl.PAYMENT_URI), eq(requestBody));
    }

    @Test
    public void testGetBalance_5xxErrorTimeout() throws PaymentProviderApiException {
        ResponseEntity<String> response = new ResponseEntity<>(body5xxErrorTimeout, HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.exchange(eq(PaymentProviderApiClientImpl.PAYMENT_URI), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(response);

        Payment payment = client.createPayment(from, to, amount);
        verify(client, times(1)).post(eq(PaymentProviderApiClientImpl.PAYMENT_URI), eq(requestBody));
        assertTrue(payment.isError());
    }

    @Test
    public void testGetBalance_5xxErrorRejection() throws PaymentProviderApiException {
        ResponseEntity<String> response = new ResponseEntity<>(body5xxErrorRejection, HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.exchange(eq(PaymentProviderApiClientImpl.PAYMENT_URI), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(response);

        Payment payment = client.createPayment(from, to, amount);
        verify(client, times(1)).post(eq(PaymentProviderApiClientImpl.PAYMENT_URI), eq(requestBody));
        assertTrue(payment.isError());
    }
}