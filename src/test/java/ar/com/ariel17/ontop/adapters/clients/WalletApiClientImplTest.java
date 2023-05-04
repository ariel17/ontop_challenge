package ar.com.ariel17.ontop.adapters.clients;

import ar.com.ariel17.ontop.core.clients.UserNotFoundException;
import ar.com.ariel17.ontop.core.clients.WalletApiException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletApiClientImplTest {

    @Mock
    private RestTemplate restTemplate;

    private WalletApiClientImpl client;

    private Long userId;

    private String balanceBodyOk;

    private String transactionBodyOk;

    private String bodyError;

    @BeforeEach
    public void setUp() {
        client = spy(new WalletApiClientImpl(restTemplate));
        userId = 10L;
        balanceBodyOk = """
                {
                  "balance": 2500,
                  "user_id": 10
                }""";
        transactionBodyOk = """
                {
                  "wallet_transaction_id": 66319,
                  "amount": 2000,
                  "user_id": 10
                }""";
        bodyError = """
                {
                  "code": "ERROR",
                  "message": "mocked error"
                }""";
    }

    @Test
    public void testGetBalance_ok() throws WalletApiException, UserNotFoundException {
        String uri = client.getBalanceUri(userId);
        ResponseEntity<String> response = new ResponseEntity<>(balanceBodyOk, HttpStatus.OK);
        when(restTemplate.exchange(eq(uri), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(response);

        BigDecimal balance = client.getBalance(userId);
        assertEquals(new BigDecimal(2500), balance);
    }

    @Test
    public void testGetBalance_error4xx() {
        String uri = client.getBalanceUri(userId);
        ResponseEntity<String> response = new ResponseEntity<>(bodyError, HttpStatus.NOT_FOUND);
        when(restTemplate.exchange(eq(uri), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(response);

        assertThrows(UserNotFoundException.class, () -> client.getBalance(userId));
    }

    @Test
    public void testGetBalance_error5xx() {
        String uri = client.getBalanceUri(userId);
        ResponseEntity<String> response = new ResponseEntity<>(bodyError, HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.exchange(eq(uri), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(response);

        assertThrows(WalletApiException.class, () -> client.getBalance(userId));
    }

    @Test
    public void testGetBalance_exchangeException() {
        String uri = client.getBalanceUri(userId);
        doThrow(new RestClientException("mocked exception")).when(restTemplate).exchange(eq(uri), eq(HttpMethod.GET), any(), eq(String.class));

        assertThrows(WalletApiException.class, () -> client.getBalance(userId));
    }

    @Test
    public void testGetBalance_2xxWithInvalidBody() {
        String uri = client.getBalanceUri(userId);
        ResponseEntity<String> response = new ResponseEntity<>("{\"invalid_body", HttpStatus.OK);
        when(restTemplate.exchange(eq(uri), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(response);

        assertThrows(WalletApiException.class, () -> client.getBalance(userId));
    }

    @Test
    public void testCreateTransaction_ok() throws WalletApiException, UserNotFoundException {
        ResponseEntity<String> response = new ResponseEntity<>(transactionBodyOk, HttpStatus.OK);
        when(restTemplate.exchange(eq(WalletApiClientImpl.TRANSACTIONS_URI), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(response);

        Long transactionId = client.createTransaction(userId, new BigDecimal(2000));
        assertEquals(66319L, transactionId);
        verify(client, times(1)).post(eq(WalletApiClientImpl.TRANSACTIONS_URI), eq("{\"amount\":2000,\"user_id\":10}"));
    }

    @Test
    public void testCreateTransaction_error5xx() {
        ResponseEntity<String> response = new ResponseEntity<>(bodyError, HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.exchange(eq(WalletApiClientImpl.TRANSACTIONS_URI), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(response);

        assertThrows(WalletApiException.class, () -> client.createTransaction(userId, new BigDecimal(2000)));
    }

    @Test
    public void testCreateTransaction_error4xx() {
        ResponseEntity<String> response = new ResponseEntity<>(bodyError, HttpStatus.NOT_FOUND);
        when(restTemplate.exchange(eq(WalletApiClientImpl.TRANSACTIONS_URI), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(response);

        assertThrows(UserNotFoundException.class, () -> client.createTransaction(userId, new BigDecimal(2000)));
    }

    @Test
    public void testCreateTransaction_2xxWithInvalidBody() {
        ResponseEntity<String> response = new ResponseEntity<>("{\"invalid_body", HttpStatus.OK);
        when(restTemplate.exchange(eq(WalletApiClientImpl.TRANSACTIONS_URI), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(response);

        assertThrows(WalletApiException.class, () -> client.createTransaction(userId, new BigDecimal(2000)));
    }

    @Test
    public void testCreateTransaction_exchangeException() {
        doThrow(new RestClientException("mocked exception")).when(restTemplate).exchange(eq(WalletApiClientImpl.TRANSACTIONS_URI), eq(HttpMethod.POST), any(), eq(String.class));

        assertThrows(WalletApiException.class, () -> client.createTransaction(userId, new BigDecimal(2000)));
    }
}