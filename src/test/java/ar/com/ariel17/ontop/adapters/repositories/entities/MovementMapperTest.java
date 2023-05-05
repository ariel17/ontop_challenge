package ar.com.ariel17.ontop.adapters.repositories.entities;

import ar.com.ariel17.ontop.core.domain.BankAccount;
import ar.com.ariel17.ontop.core.domain.Movement;
import ar.com.ariel17.ontop.core.domain.Operation;
import ar.com.ariel17.ontop.core.domain.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MovementMapperTest {

    private Movement movement;

    @BeforeEach
    public void setUp() {
        BankAccount from = new BankAccount("0123456789", "012345678", Currency.getInstance("USD"));
        BankAccount to = new BankAccount("9876543210", "876543210", Currency.getInstance("USD"));
        movement = new Movement(10L, 99L, Type.TRANSFER, Operation.WITHDRAW, Currency.getInstance("USD"), new BigDecimal("-100.01"), from, to, 5000L, UUID.randomUUID(), new Date());
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

        assertNotNull(movement.getFrom().getRouting());
        assertNotNull(entity.getFromRouting());
        assertEquals(movement.getFrom().getRouting(), entity.getFromRouting());

        assertNotNull(movement.getFrom().getAccount());
        assertNotNull(entity.getFromAccount());
        assertEquals(movement.getFrom().getAccount(), entity.getFromAccount());

        assertNotNull(movement.getFrom().getCurrency());
        assertNotNull(entity.getCurrency());
        assertEquals(movement.getFrom().getCurrency(), entity.getCurrency());

        assertNotNull(movement.getTo().getRouting());
        assertNotNull(entity.getToRouting());
        assertEquals(movement.getTo().getRouting(), entity.getToRouting());

        assertNotNull(movement.getTo().getAccount());
        assertNotNull(entity.getToAccount());
        assertEquals(movement.getTo().getAccount(), entity.getToAccount());

        assertNotNull(movement.getTo().getCurrency());
        assertEquals(movement.getTo().getCurrency(), entity.getCurrency());

        assertNotNull(movement.getWalletTransactionId());
        assertNotNull(entity.getWalletTransactionId());
        assertEquals(movement.getWalletTransactionId(), entity.getWalletTransactionId());

        assertNotNull(movement.getPaymentId());
        assertNotNull(entity.getPaymentId());
        assertEquals(movement.getPaymentId(), entity.getPaymentId());

        assertNotNull(movement.getCreatedAt());
        assertNotNull(entity.getCreatedAt());
        assertEquals(movement.getCreatedAt(), entity.getCreatedAt());
    }
}
