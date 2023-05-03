package ar.com.ariel17.ontop.adapters.repositories;

import ar.com.ariel17.ontop.adapters.repositories.entities.BankAccountOwnerEntity;
import ar.com.ariel17.ontop.adapters.repositories.jpa.JpaBankAccountRepository;
import ar.com.ariel17.ontop.core.domain.BankAccount;
import ar.com.ariel17.ontop.core.domain.BankAccountOwner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Currency;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BankAccountOwnerRepositoryImplTest {

    @Mock
    private JpaBankAccountRepository jpaRepository;

    private BankAccountRepositoryImpl repository;

    private BankAccountOwner owner;

    private Long ownerId;

    @BeforeEach
    public void setUp() {
        repository = new BankAccountRepositoryImpl(jpaRepository);

        BankAccount account = new BankAccount(4321L, 4321L, Currency.getInstance("USD"));
        owner = new BankAccountOwner(null, 999L, account, "1234ABC", "John", "Snow", null);
    }

    @Test
    public void testSave_ok() {
        repository.save(owner);
        verify(jpaRepository, times(1)).saveAndFlush(any(BankAccountOwnerEntity.class));
    }

    @Test
    public void testGetById_ok() {
        repository.getById(ownerId);
        verify(jpaRepository, times(1)).findById(eq(ownerId));
    }
}
