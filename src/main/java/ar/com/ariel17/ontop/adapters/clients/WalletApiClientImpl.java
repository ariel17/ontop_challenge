package ar.com.ariel17.ontop.adapters.clients;

import ar.com.ariel17.ontop.adapters.clients.entities.requests.WalletTransactionRequest;
import ar.com.ariel17.ontop.adapters.clients.entities.responses.WalletBalanceResponse;
import ar.com.ariel17.ontop.adapters.clients.entities.responses.WalletTransactionResponse;
import ar.com.ariel17.ontop.core.clients.UserNotFoundException;
import ar.com.ariel17.ontop.core.clients.WalletApiClient;
import ar.com.ariel17.ontop.core.clients.WalletApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

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

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                throw new WalletApiException("Invalid request to Wallet API", e);
            }
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new UserNotFoundException(String.format("User ID=%d not found", userId));
            }
            throw new WalletApiException(e);

        } catch (Exception e) {
            throw new WalletApiException(e);
        }

        String body = response.getBody();
        WalletBalanceResponse balance;
        try {
            balance = mapper.readValue(body, WalletBalanceResponse.class);
        } catch (Exception e) {
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

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                throw new WalletApiException("Invalid request to Wallet API", e);
            }

            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new UserNotFoundException(String.format("User ID=%d not found", userId));
            }

            throw new WalletApiException(e);

        } catch (Exception e) {
            throw new WalletApiException(e);
        }

        String responseBody = response.getBody();
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
