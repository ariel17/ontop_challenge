package ar.com.ariel17;

import ar.com.ariel17.core.domain.BankAccount;
import ar.com.ariel17.core.domain.BankAccountOwner;
import ar.com.ariel17.core.services.TransactionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Currency;

@Configuration
public class AppConfig {

    @Value("${on_top.routing}")
    private Long onTopRoutingNumber;

    @Value("${on_top.account}")
    private Long onTopAccountNumber;

    @Value("${on_top.currency}")
    private String currencyCode;

    @Value("${on_top.name}")
    private String onTopName;

    @Value("${transactions.fee_percent}")
    private String feePercent;

    @Bean
    public BankAccountOwner onTopBankAccount() {
        BankAccount account = new BankAccount(onTopRoutingNumber, onTopAccountNumber, Currency.getInstance(currencyCode));
        return new BankAccountOwner(null, 0L, account, "", onTopName, "", null);
    }

    @Bean
    public TransactionFactory transactionFactory() {
        BigDecimal percent = new BigDecimal(feePercent);
        return new TransactionFactory(percent);
    }
}
