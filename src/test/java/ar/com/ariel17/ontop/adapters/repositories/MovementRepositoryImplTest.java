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
import java.util.UUID;

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
        BankAccount from = new BankAccount(1234L, 1234L, currency);
        BankAccount to = new BankAccount(4321L, 4321L, currency);
        movement = new Movement(null, 1234L, Type.TRANSFER, Operation.EGRESS, currency, amount, from, to, 500L, UUID.randomUUID(), null);

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
