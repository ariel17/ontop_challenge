package ar.com.ariel17.ontop.core.services;

import ar.com.ariel17.ontop.core.domain.BankAccount;
import ar.com.ariel17.ontop.core.domain.BankAccountOwner;
import ar.com.ariel17.ontop.core.repositories.BankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Currency;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BankAccountServiceImplTest {

    @Mock
    private BankAccountRepository repository;

    @InjectMocks
    private BankAccountServiceImpl service;

    private BankAccountOwner owner;

    @BeforeEach
    public void setUp() {
        BankAccount account = new BankAccount(1234L, 1234L, Currency.getInstance("USD"));
        owner = new BankAccountOwner(null, 1234L, account, "1234", "John", "Doe", null);
    }

    @Test
    public void testCreateRecipient_ok() throws BankAccountException {
        service.create(owner);
        verify(repository, times(1)).save(eq(owner));
    }
}