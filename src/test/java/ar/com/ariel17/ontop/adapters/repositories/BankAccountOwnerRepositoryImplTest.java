package ar.com.ariel17.ontop.adapters.repositories;

import ar.com.ariel17.ontop.adapters.repositories.entities.BankAccountOwnerEntity;
import ar.com.ariel17.ontop.adapters.repositories.entities.BankAccountOwnerMapper;
import ar.com.ariel17.ontop.adapters.repositories.jpa.JpaBankAccountRepository;
import ar.com.ariel17.ontop.core.domain.BankAccount;
import ar.com.ariel17.ontop.core.domain.BankAccountOwner;
import ar.com.ariel17.ontop.core.repositories.BankAccountOwnerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Currency;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BankAccountOwnerRepositoryImplTest {

    @Mock
    private JpaBankAccountRepository jpaRepository;

    private BankAccountRepositoryImpl repository;

    private BankAccountOwner owner;

    private Long ownerId;

    private Long userId;

    @BeforeEach
    public void setUp() {
        repository = new BankAccountRepositoryImpl(jpaRepository);
        ownerId = 1000L;
        userId = 999L;

        BankAccount account = BankAccount.builder().
                routing("0123456789").
                account("012345678").
                currency(Currency.getInstance("USD")).
                build();
        owner = BankAccountOwner.builder().
                id(ownerId).
                userId(userId).
                bankAccount(account).
                idNumber("123ABC").
                firstName("John").
                lastName("Snow").
                build();
    }

    @Test
    public void testSave_ok() {
        repository.save(owner);
        verify(jpaRepository, times(1)).saveAndFlush(any(BankAccountOwnerEntity.class));
    }

    @Test
    public void testGetByIdAndUserId_ok() throws BankAccountOwnerNotFoundException {
        BankAccountOwnerEntity entity = BankAccountOwnerMapper.INSTANCE.bankAccountOwnerToBankAccountOwnerEntity(owner);
        when(jpaRepository.findById(eq(ownerId))).thenReturn(Optional.of(entity));
        repository.getByIdAndUserId(ownerId, userId);
        verify(jpaRepository, times(1)).findById(eq(ownerId));
    }
}
