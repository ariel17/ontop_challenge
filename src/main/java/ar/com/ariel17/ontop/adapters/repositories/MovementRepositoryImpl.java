package ar.com.ariel17.ontop.adapters.repositories;

import ar.com.ariel17.ontop.adapters.repositories.entities.MovementEntity;
import ar.com.ariel17.ontop.adapters.repositories.entities.MovementMapper;
import ar.com.ariel17.ontop.adapters.repositories.jpa.JpaMovementRepository;
import ar.com.ariel17.ontop.core.domain.Movement;
import ar.com.ariel17.ontop.core.domain.Transaction;
import ar.com.ariel17.ontop.core.repositories.MovementRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class MovementRepositoryImpl implements MovementRepository {

    @Autowired
    private JpaMovementRepository repository;

    @Override
    public Movement save(Movement obj) {
        MovementEntity entity = MovementMapper.INSTANCE.movementToMovementEntity(obj);
        entity = repository.saveAndFlush(entity);
        return MovementMapper.INSTANCE.movementEntityToMovement(entity);
    }

    @Override
    public Transaction save(Transaction transaction) {
        List<MovementEntity> entities = MovementMapper.INSTANCE.movementsToMovementEntities(transaction.getMovements());
        entities = repository.saveAllAndFlush(entities);
        List<Movement> movements = MovementMapper.INSTANCE.movementEntitiesToMovements(entities);
        transaction.setMovements(movements);
        return transaction;
    }
}
