package ar.com.ariel17.ontop.adapters.clients;

import ar.com.ariel17.ontop.core.clients.WalletApi;
import ar.com.ariel17.ontop.core.clients.WalletApiException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class WalletApiImpl implements WalletApi {

    @Override
    public BigDecimal getBalance(Long userId) throws WalletApiException {
        return null;
    }

    @Override
    public Long createTransaction(Long userId, BigDecimal amount) throws WalletApiException {
        return null;
    }
}
