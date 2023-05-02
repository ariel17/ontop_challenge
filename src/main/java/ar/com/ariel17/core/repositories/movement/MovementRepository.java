package ar.com.ariel17.core.repositories.movement;

import ar.com.ariel17.core.domain.transaction.Movement;
import ar.com.ariel17.core.domain.transaction.Transaction;
import ar.com.ariel17.core.repositories.DatabaseRepository;

public interface MovementRepository extends DatabaseRepository<Movement, MovementRepositoryException> {

    Transaction save(Transaction transaction) throws MovementRepositoryException;
}
