package ar.com.ariel17.ontop.adapters.repositories;

import ar.com.ariel17.ontop.core.domain.Movement;
import ar.com.ariel17.ontop.core.domain.Transaction;
import ar.com.ariel17.ontop.core.repositories.MovementRepository;
import ar.com.ariel17.ontop.core.repositories.MovementRepositoryException;
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
