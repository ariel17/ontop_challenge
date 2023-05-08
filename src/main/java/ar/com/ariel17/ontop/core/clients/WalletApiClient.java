package ar.com.ariel17.ontop.core.clients;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Wallet API client methods for implementors.
 */
@Component
public interface WalletApiClient {

    BigDecimal getBalance(Long userId) throws UserNotFoundException, WalletApiException;

    Long createTransaction(Long userId, BigDecimal amount) throws UserNotFoundException, WalletApiException;
}
