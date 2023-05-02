package ar.com.ariel17.core.repositories;

import ar.com.ariel17.core.domain.Movement;
import ar.com.ariel17.core.domain.Transaction;

public interface MovementRepository extends DatabaseRepository<Movement, MovementRepositoryException> {

    Transaction save(Transaction transaction) throws MovementRepositoryException;
}
