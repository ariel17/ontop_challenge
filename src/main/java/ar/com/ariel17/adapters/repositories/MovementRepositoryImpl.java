package ar.com.ariel17.adapters.repositories;

import ar.com.ariel17.core.domain.Movement;
import ar.com.ariel17.core.domain.Transaction;
import ar.com.ariel17.core.repositories.MovementRepository;
import ar.com.ariel17.core.repositories.MovementRepositoryException;
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
