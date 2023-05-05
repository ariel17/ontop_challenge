package ar.com.ariel17.ontop.adapters.repositories;

import ar.com.ariel17.ontop.adapters.repositories.entities.MovementEntity;
import ar.com.ariel17.ontop.adapters.repositories.jpa.JpaMovementRepository;
import ar.com.ariel17.ontop.core.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Currency;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovementRepositoryImplTest {

    @Mock
    private JpaMovementRepository jpaRepository;

    private MovementRepositoryImpl repository;

    private Movement movement;

    private Transaction transaction;

    @BeforeEach
    public void setUp() {
        repository = new MovementRepositoryImpl(jpaRepository);

        Currency currency = Currency.getInstance("USD");
        BigDecimal amount = new BigDecimal("1000");

        BankAccount account = BankAccount.builder().
                routing("0123456789").
                account("012345678").
                type(BankAccountType.COMPANY).
                currency(Currency.getInstance("USD")).build();
        BankAccountOwner onTopAccount = BankAccountOwner.builder().
                userId(10L).
                bankAccount(account).
                firstName("ONTOP INC").build();

        account = BankAccount.builder().
                routing("9876543210").
                account("876543210").
                type(BankAccountType.COMPANY).
                currency(Currency.getInstance("USD")).build();
        BankAccountOwner externalAccount = BankAccountOwner.builder().
                userId(10L).
                bankAccount(account).
                idNumber("123ABC").
                firstName("John").
                lastName("Snow").build();

        movement = Movement.builder().
                userId(4321L).
                type(MovementType.TRANSFER).
                operation(Operation.WITHDRAW).
                currency(currency).
                amount(amount).
                onTopAccount(onTopAccount).
                externalAccount(externalAccount).build();

        transaction = new Transaction();
        transaction.addMovement(movement);
    }

    @Test
    public void testSaveMovement_ok() {
        repository.save(movement);
        verify(jpaRepository, times(1)).saveAndFlush(any(MovementEntity.class));
    }

    @Test
    public void testSaveTransaction_ok() {
        repository.save(transaction);
        verify(jpaRepository, times(1)).saveAllAndFlush(anyList());
    }
}
