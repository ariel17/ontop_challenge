package ar.com.ariel17.ontop.core.services;

import ar.com.ariel17.ontop.core.domain.BankAccount;
import ar.com.ariel17.ontop.core.domain.BankAccountOwner;
import ar.com.ariel17.ontop.core.repositories.BankAccountOwnerNotFoundException;
import ar.com.ariel17.ontop.core.repositories.BankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BankAccountServiceImplTest {

    @Mock
    private BankAccountRepository repository;

    @InjectMocks
    private BankAccountServiceImpl service;

    private BankAccountOwner owner;

    private Long ownerId;

    @BeforeEach
    public void setUp() {
        ownerId = 1234L;
        BankAccount account = BankAccount.builder().
                account(1234L).
                routing(1234L).
                currency(Currency.getInstance("USD")).
                build();
        owner = BankAccountOwner.builder().
                userId(999L).
                bankAccount(account).
                idNumber("1234").
                firstName("John").
                lastName("Snow").
                build();
    }

    @Test
    public void testCreateRecipient_ok() {
        service.create(owner);
        verify(repository, times(1)).save(eq(owner));
    }

    @Test
    public void testGetById_found() throws BankAccountOwnerNotFoundException {
        when(repository.getById(eq(ownerId))).thenReturn(owner);
        var o = service.getById(ownerId);
        assertNotNull(o);
        verify(repository, times(1)).getById(eq(ownerId));
    }

    @Test
    public void testGetById_notPresent() throws BankAccountOwnerNotFoundException {
        doThrow(new BankAccountOwnerNotFoundException("not found")).when(repository).getById(eq(ownerId));
        assertThrows(BankAccountOwnerNotFoundException.class, () -> service.getById(ownerId));
        verify(repository, times(1)).getById(eq(ownerId));
    }
}