package ar.com.ariel17.ontop.core.clients;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public interface WalletApiClient {

    BigDecimal getBalance(Long userId) throws WalletApiException;

    Long createTransaction(Long userId, BigDecimal amount) throws WalletApiException;
}
