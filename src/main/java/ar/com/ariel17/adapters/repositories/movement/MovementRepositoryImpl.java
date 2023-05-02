package ar.com.ariel17.adapters.repositories.movement;

import ar.com.ariel17.core.domain.transaction.Movement;
import ar.com.ariel17.core.domain.transaction.Transaction;
import ar.com.ariel17.core.repositories.movement.MovementRepository;
import ar.com.ariel17.core.repositories.movement.MovementRepositoryException;
import org.springframework.stereotype.Component;

@Component
public class MovementRepositoryImpl implements MovementRepository {

    @Override
    public Movement save(Movement obj) throws MovementRepositoryException {
        return null;
    }

    @Override
    public Transaction save(Transaction transaction) throws MovementRepositoryException {
        return null;
    }
}
