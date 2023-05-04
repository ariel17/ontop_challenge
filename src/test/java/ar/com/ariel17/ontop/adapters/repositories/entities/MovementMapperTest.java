package ar.com.ariel17.ontop.adapters.repositories.entities;

import ar.com.ariel17.ontop.core.domain.BankAccount;
import ar.com.ariel17.ontop.core.domain.Movement;
import ar.com.ariel17.ontop.core.domain.Operation;
import ar.com.ariel17.ontop.core.domain.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class MovementMapperTest {

    private Movement movement;

    @BeforeEach
    public void setUp() {
        BankAccount from = new BankAccount(1111L, 1111L, Currency.getInstance("USD"));
        BankAccount to = new BankAccount(2222L, 2222L, Currency.getInstance("USD"));
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
        // TODO assert not null, not only equals
        assertEquals(movement.getId(), entity.getId());
        assertEquals(movement.getUserId(), entity.getUserId());
        assertEquals(movement.getType(), entity.getType());
        assertEquals(movement.getOperation(), entity.getOperation());
        assertEquals(movement.getCurrency(), entity.getCurrency());
        assertEquals(movement.getAmount(), entity.getAmount());
        assertEquals(movement.getFrom().getRouting(), entity.getFromRouting());
        assertEquals(movement.getFrom().getAccount(), entity.getFromAccount());
        assertEquals(movement.getFrom().getCurrency(), entity.getCurrency());
        assertEquals(movement.getTo().getRouting(), entity.getToRouting());
        assertEquals(movement.getTo().getAccount(), entity.getToAccount());
        assertEquals(movement.getTo().getCurrency(), entity.getCurrency());
        assertEquals(movement.getWalletTransactionId(), entity.getWalletTransactionId());
        assertEquals(movement.getPaymentId(), entity.getPaymentId());
        assertEquals(movement.getCreatedAt(), entity.getCreatedAt());
    }
}
