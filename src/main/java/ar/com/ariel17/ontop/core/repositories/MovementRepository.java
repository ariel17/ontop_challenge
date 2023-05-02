package ar.com.ariel17.ontop.core.repositories;

import ar.com.ariel17.ontop.core.domain.Movement;
import ar.com.ariel17.ontop.core.domain.Transaction;

public interface MovementRepository extends DatabaseRepository<Movement> {

    Transaction save(Transaction transaction);
}
