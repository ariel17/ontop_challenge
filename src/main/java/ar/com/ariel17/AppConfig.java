package ar.com.ariel17;

import ar.com.ariel17.core.domain.BankAccount;
import ar.com.ariel17.core.domain.BankAccountOwner;
import ar.com.ariel17.core.services.TransactionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Currency;

@Configuration
public class AppConfig {

    @Bean
    public BankAccountOwner onTopBankAccount() {
        // TODO move parameters to config file
        BankAccount account = new BankAccount(1234L, 1234L, Currency.getInstance("USD"));
        return new BankAccountOwner(null, 0L, account, "", "ON TOP INC", "", null);
    }

    @Bean
    public TransactionFactory transactionFactory() {
        BigDecimal feePercent = new BigDecimal("0.1");
        return new TransactionFactory(feePercent);
    }
}
