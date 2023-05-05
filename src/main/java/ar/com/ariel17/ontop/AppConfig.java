package ar.com.ariel17.ontop;

import ar.com.ariel17.ontop.adapters.repositories.LockRepositoryImpl;
import ar.com.ariel17.ontop.core.domain.BankAccount;
import ar.com.ariel17.ontop.core.domain.BankAccountOwner;
import ar.com.ariel17.ontop.core.repositories.LockRepository;
import ar.com.ariel17.ontop.core.services.TransactionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.integration.support.locks.ExpirableLockRegistry;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Currency;

@Configuration
public class AppConfig implements ApplicationContextAware {

    private static final String LOCK_NAME = "lock";

    private ApplicationContext context;

    @Value("${on_top.routing}")
    private String onTopRoutingNumber;

    @Value("${on_top.account}")
    private String onTopAccountNumber;

    @Value("${on_top.currency}")
    private String onTopCurrencyCode;

    @Value("${on_top.name}")
    private String onTopName;

    @Value("${transactions.fee_percent}")
    private String feePercent;

    @Value("${lock.release_time_seconds}")
    private int lockReleaseTimeSeconds;

    @Value("${payment_provider.host}")
    private String paymentProviderHost;

    @Value("${payment_provider.connection_timeout_ms}")
    private int paymentProviderConnectionTimeout;

    @Value("${payment_provider.read_timeout_ms}")
    private int paymentProviderReadTimeout;

    @Value("${wallet.host}")
    private String walletHost;

    @Value("${wallet.connection_timeout_ms}")
    private int walletConnectionTimeout;

    @Value("${wallet.read_timeout_ms}")
    private int walletReadTimeout;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Bean
    public Currency defaultCurrency() {
        return Currency.getInstance(onTopCurrencyCode);
    }

    @Bean
    public BankAccountOwner onTopBankAccount() {
        BankAccount account = BankAccount.builder().
                routing(onTopRoutingNumber).
                account(onTopAccountNumber).
                currency(Currency.getInstance(onTopCurrencyCode)).
                build();

        return BankAccountOwner.builder().
                bankAccount(account).
                idNumber("").
                firstName(onTopName).
                lastName("").
                build();
    }

    @Bean
    public TransactionFactory transactionFactory() {
        BigDecimal percent = new BigDecimal(feePercent);
        return new TransactionFactory(percent);
    }

    @Bean(destroyMethod = "destroy")
    public ExpirableLockRegistry redisLockRegistry(RedisConnectionFactory redisConnectionFactory) {
        var releaseTime = Duration.ofSeconds(lockReleaseTimeSeconds).toMillis();
        return new RedisLockRegistry(redisConnectionFactory, LOCK_NAME, releaseTime);
    }

    @Bean
    @DependsOn({"redisLockRegistry"})
    @Scope("prototype")
    public LockRepository lockRepository(Long userId) {
        ExpirableLockRegistry lockRegistry = context.getBean(ExpirableLockRegistry.class);
        return new LockRepositoryImpl(lockRegistry, userId);
    }

    @Bean(name = "paymentProviderRestTemplate")
    public RestTemplate paymentProviderRestTemplate(RestTemplateBuilder builder) {
        return builder.
                setConnectTimeout(Duration.ofMillis(paymentProviderConnectionTimeout)).
                setReadTimeout(Duration.ofMillis(paymentProviderReadTimeout)).
                build();
    }

    @Bean(name = "walletRestTemplate")
    public RestTemplate walletRestTemplate(RestTemplateBuilder builder) {
        return builder.
                setConnectTimeout(Duration.ofMillis(walletConnectionTimeout)).
                setReadTimeout(Duration.ofMillis(walletReadTimeout)).
                rootUri(walletHost).
                build();
    }
}
