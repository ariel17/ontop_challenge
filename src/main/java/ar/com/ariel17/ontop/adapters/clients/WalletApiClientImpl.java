package ar.com.ariel17.ontop.adapters.clients;

import ar.com.ariel17.ontop.adapters.clients.entities.requests.WalletTransactionRequest;
import ar.com.ariel17.ontop.adapters.clients.entities.responses.WalletBalanceResponse;
import ar.com.ariel17.ontop.adapters.clients.entities.responses.WalletTransactionResponse;
import ar.com.ariel17.ontop.core.clients.WalletApiClient;
import ar.com.ariel17.ontop.core.clients.WalletApiException;
import ar.com.ariel17.ontop.core.clients.UserNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;

@Component
public class WalletApiClientImpl extends ApiClient implements WalletApiClient {

    private static final String BALANCE_URI_TEMPLATE = "/wallets/balance?user_id=%d";

    protected static final String TRANSACTIONS_URI = "/wallets/transactions";

    @Autowired
    public WalletApiClientImpl(RestTemplate walletRestTemplate) {
        super(walletRestTemplate);
        mapper = new ObjectMapper();
    }

    @Override
    public BigDecimal getBalance(Long userId) throws UserNotFoundException, WalletApiException {
        ResponseEntity<String> response;
        try {
            response = this.get(getBalanceUri(userId));
        } catch (Exception e) {
            throw new WalletApiException(e);
        }

        HttpStatusCode statusCode = response.getStatusCode();
        if (statusCode.is4xxClientError()) {
            throw new UserNotFoundException(String.format("User ID=%d not found", userId));
        }

        String body = response.getBody();
        if (statusCode.is5xxServerError()) {
            throw new WalletApiException(String.format("Wallet API error: status_code=%d, body=%s", statusCode.value(), body));
        }

        WalletBalanceResponse balance;
        try {
            balance = mapper.readValue(body, WalletBalanceResponse.class);
        } catch (IOException e) {
            throw new WalletApiException(e);
        }

        return balance.getBalance();
    }

    @Override
    public Long createTransaction(Long userId, BigDecimal amount) throws UserNotFoundException, WalletApiException {
        WalletTransactionRequest transactionRequest = new WalletTransactionRequest(amount, userId);

        String requestBody;
        try {
            requestBody = mapper.writeValueAsString(transactionRequest);
        } catch (JsonProcessingException e) {
            throw new WalletApiException(e);
        }

        ResponseEntity<String> response;
        try {
            response = post(TRANSACTIONS_URI, requestBody);
        } catch (Exception e) {
            throw new WalletApiException(e);
        }

        HttpStatusCode statusCode = response.getStatusCode();
        if (statusCode.is4xxClientError()) {
            throw new UserNotFoundException(String.format("User ID=%d not found", userId));
        }

        String responseBody = response.getBody();
        if (statusCode.is5xxServerError()) {
            String message = String.format("Wallet API error: status_code=%d, body=%s", statusCode.value(), responseBody);
            throw new WalletApiException(message);
        }

        WalletTransactionResponse transactionResponse;
        try {
            transactionResponse = mapper.readValue(responseBody, WalletTransactionResponse.class);
        } catch (JsonProcessingException e) {
            throw new WalletApiException(e);
        }

        return transactionResponse.getWalletTransactionId();
    }

    protected String getBalanceUri(Long userId) {
        return String.format(BALANCE_URI_TEMPLATE, userId);
    }
}
