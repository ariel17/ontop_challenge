package ar.com.ariel17.core.repositories.movement;

import ar.com.ariel17.core.domain.transaction.Movement;
import ar.com.ariel17.core.repositories.DatabaseRepository;

import java.util.List;

public interface MovementRepository extends DatabaseRepository<Movement, MovementRepositoryException> {

    void save(List<Movement> movements) throws MovementRepositoryException;
}
