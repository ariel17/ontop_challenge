package ar.com.ariel17.core.clients.wallet;

import java.math.BigDecimal;

public interface WalletApi {

    BigDecimal getBalance(Long userId);

    Long createTransaction(Long userId, BigDecimal amount) throws WalletApiException;
}
