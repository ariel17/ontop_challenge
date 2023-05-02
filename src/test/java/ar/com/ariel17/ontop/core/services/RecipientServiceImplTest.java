package ar.com.ariel17.ontop.core.services;

import ar.com.ariel17.ontop.core.domain.BankAccount;
import ar.com.ariel17.ontop.core.domain.BankAccountOwner;
import ar.com.ariel17.ontop.core.repositories.RecipientRepository;
import ar.com.ariel17.ontop.core.repositories.RecipientRepositoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipientServiceImplTest {

    @Mock
    private RecipientRepository repository;

    @InjectMocks
    private RecipientServiceImpl service;

    private BankAccountOwner owner;

    @BeforeEach
    public void setUp() {
        BankAccount account = new BankAccount(1234L, 1234L, Currency.getInstance("USD"));
        owner = new BankAccountOwner(null, 1234L, account, "1234", "John", "Doe", null);
    }

    @Test
    public void testCreateRecipient_ok() throws RecipientException, RecipientRepositoryException {
        service.createRecipient(owner);
        verify(repository, times(1)).save(eq(owner));
    }

    @Test
    public void testCreateRecipient_null() {
        assertThrows(IllegalArgumentException.class, () -> service.createRecipient(null));
    }

    @Test
    public void testCreateRecipient_failed() throws RecipientRepositoryException {
        doThrow(new RecipientRepositoryException("mocked exception")).when(repository).save(any(BankAccountOwner.class));
        assertThrows(RecipientException.class, () -> service.createRecipient(owner));
    }
}