package ar.com.ariel17.adapters.clients.wallet;

import ar.com.ariel17.core.clients.wallet.WalletApi;
import ar.com.ariel17.core.clients.wallet.WalletApiException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class WalletApiImpl implements WalletApi {

    @Override
    public BigDecimal getBalance(Long userId) {
        return null;
    }

    @Override
    public Long createTransaction(Long userId, BigDecimal amount) throws WalletApiException {
        return null;
    }
}
