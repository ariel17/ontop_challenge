package ar.com.ariel17.ontop.adapters.clients;

import ar.com.ariel17.ontop.adapters.clients.entities.requests.WalletTransactionRequest;
import ar.com.ariel17.ontop.adapters.clients.entities.responses.WalletBalanceResponse;
import ar.com.ariel17.ontop.adapters.clients.entities.responses.WalletTransactionResponse;
import ar.com.ariel17.ontop.core.clients.WalletApiClient;
import ar.com.ariel17.ontop.core.clients.WalletApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
    public BigDecimal getBalance(Long userId) throws WalletApiException {
        String body;

        try {
            body = this.get(getBalanceUri(userId));
        } catch (IllegalStateException e) {
            throw new WalletApiException(e);
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
    public Long createTransaction(Long userId, BigDecimal amount) throws WalletApiException {
        WalletTransactionRequest body = new WalletTransactionRequest(amount, userId);

        String requestBody;
        try {
            requestBody = mapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new WalletApiException(e);
        }

        String responseBody;
        try {
            responseBody = post(TRANSACTIONS_URI, requestBody, true);
        } catch (IllegalStateException e) {
            throw new WalletApiException(e);
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
