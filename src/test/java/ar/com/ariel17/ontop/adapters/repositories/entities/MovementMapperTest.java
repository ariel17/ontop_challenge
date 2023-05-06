package ar.com.ariel17.ontop.adapters.repositories.entities;

import ar.com.ariel17.ontop.core.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class MovementMapperTest {

    private Movement movement;

    @BeforeEach
    public void setUp() {
        Currency currency = Currency.getInstance("USD");
        BankAccount account1 = BankAccount.builder().
                routing("0123456789").
                account("012345678").
                type(BankAccountType.COMPANY).
                currency(currency).build();
        BankAccountOwner onTopAccount = BankAccountOwner.builder().
                id(1L).
                userId(10L).
                bankAccount(account1).
                firstName("ONTOP INC").build();

        BankAccount account2 = BankAccount.builder().
                routing("9876543210").
                account("876543210").
                currency(currency).build();
        BankAccountOwner externalAccount = BankAccountOwner.builder().
                id(2L).
                userId(10L).
                bankAccount(account2).
                idNumber("123ABC").
                firstName("John").
                lastName("Snow").build();

        Payment payment = Payment.builder().
                id(UUID.randomUUID()).
                status(PaymentStatus.PROCESSING).
                createdAt(new Date()).
                amount(new BigDecimal(1000)).build();

        movement = Movement.builder().
                id(99L).
                userId(4321L).
                type(MovementType.TRANSFER).
                operation(Operation.WITHDRAW).
                currency(currency).
                amount(new BigDecimal(1000)).
                onTopAccount(onTopAccount).
                externalAccount(externalAccount).
                walletTransactionId(100L).
                createdAt(new Date()).
                payment(payment).build();
    }

    @Test
    public void testMovementToMovementEntity() {
        MovementEntity entity = MovementMapper.INSTANCE.movementToMovementEntity(movement);
        compare(movement, entity);
    }

    @Test
    public void testMovementEntityToMovement() {
        MovementEntity entity = MovementMapper.INSTANCE.movementToMovementEntity(movement);
        Movement m2 = MovementMapper.INSTANCE.movementEntityToMovement(entity);
        compare(m2, entity);
    }

    @Test
    public void testMovementsToMovementEntities() {
        List<Movement> movements = new ArrayList<>(){{add(movement);}};
        List<MovementEntity> entities = MovementMapper.INSTANCE.movementsToMovementEntities(movements);
        assertEquals(movements.size(), entities.size());
        compare(movements.get(0), entities.get(0));
    }

    @Test
    public void testMovementEntitiesToMovements() {
        MovementEntity entity = MovementMapper.INSTANCE.movementToMovementEntity(movement);
        List<MovementEntity> entities = new ArrayList<>(){{add(entity);}};
        List<Movement> movements = MovementMapper.INSTANCE.movementEntitiesToMovements(entities);
        assertEquals(movements.size(), entities.size());
        compare(movements.get(0), entities.get(0));
    }

    private void compare(Movement movement, MovementEntity entity) {
        assertNotNull(movement.getId());
        assertNotNull(entity.getId());
        assertEquals(movement.getId(), entity.getId());

        assertNotNull(movement.getUserId());
        assertNotNull(entity.getUserId());
        assertEquals(movement.getUserId(), entity.getUserId());

        assertNotNull(movement.getType());
        assertNotNull(entity.getType());
        assertEquals(movement.getType(), entity.getType());

        assertNotNull(movement.getOperation());
        assertNotNull(entity.getOperation());
        assertEquals(movement.getOperation(), entity.getOperation());

        assertNotNull(movement.getCurrency());
        assertNotNull(entity.getCurrency());
        assertEquals(movement.getCurrency(), entity.getCurrency());

        assertNotNull(movement.getAmount());
        assertNotNull(entity.getAmount());
        assertEquals(movement.getAmount(), entity.getAmount());

        BankAccountOwner onTopM = movement.getOnTopAccount();
        BankAccountOwnerEntity onTopE = entity.getOnTopAccount();
        assertNotNull(onTopM.getId());
        assertNotNull(onTopE.getId());
        assertEquals(onTopM.getId(), onTopE.getId());

        assertNotNull(onTopM.getBankAccount().getRouting());
        assertNotNull(onTopE.getRouting());
        assertEquals(onTopM.getBankAccount().getRouting(), onTopE.getRouting());

        assertNotNull(onTopM.getBankAccount().getAccount());
        assertNotNull(onTopE.getAccount());
        assertEquals(onTopM.getBankAccount().getAccount(), onTopE.getAccount());

        assertNotNull(onTopM.getBankAccount().getType());
        assertNotNull(onTopE.getType());
        assertEquals(onTopM.getBankAccount().getType(), onTopE.getType());

        assertNotNull(onTopM.getBankAccount().getCurrency());
        assertNotNull(onTopE.getCurrency());
        assertEquals(onTopM.getBankAccount().getCurrency(), onTopE.getCurrency());

        BankAccountOwner externalM = movement.getExternalAccount();
        BankAccountOwnerEntity externalE = entity.getExternalAccount();
        assertNotNull(externalM.getId());
        assertNotNull(externalE.getId());
        assertEquals(externalM.getId(), externalE.getId());

        assertNotNull(externalM.getBankAccount().getRouting());
        assertNotNull(externalE.getRouting());
        assertEquals(externalM.getBankAccount().getRouting(), externalE.getRouting());

        assertNotNull(externalM.getBankAccount().getAccount());
        assertNotNull(externalE.getAccount());
        assertEquals(externalM.getBankAccount().getAccount(), externalE.getAccount());

        assertNull(externalM.getBankAccount().getType());
        assertNull(externalE.getType());

        assertNotNull(externalM.getBankAccount().getCurrency());
        assertNotNull(externalE.getCurrency());
        assertEquals(externalM.getBankAccount().getCurrency(), externalE.getCurrency());

        assertNotNull(movement.getWalletTransactionId());
        assertNotNull(entity.getWalletTransactionId());
        assertEquals(movement.getWalletTransactionId(), entity.getWalletTransactionId());

        assertNotNull(movement.getPayment());
        assertNotNull(entity.getPayment());

        Payment pM = movement.getPayment();
        PaymentEntity pE = entity.getPayment();
        assertEquals(pM.getId(), pE.getId());
        assertEquals(pM.getStatus(), pE.getStatus());
        assertEquals(pM.getError(), pE.getError());
        assertEquals(pM.getAmount(), pE.getAmount());
        assertEquals(pM.getCreatedAt(), pE.getCreatedAt());

        assertNotNull(movement.getCreatedAt());
        assertNotNull(entity.getCreatedAt());
        assertEquals(movement.getCreatedAt(), entity.getCreatedAt());
    }
}
