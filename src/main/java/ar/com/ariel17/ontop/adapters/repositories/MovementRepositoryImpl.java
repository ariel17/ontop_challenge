package ar.com.ariel17.ontop.adapters.repositories;

import ar.com.ariel17.ontop.core.domain.Movement;
import ar.com.ariel17.ontop.core.domain.Transaction;
import ar.com.ariel17.ontop.core.repositories.MovementRepository;
import org.springframework.stereotype.Component;

@Component
public class MovementRepositoryImpl implements MovementRepository {

    @Override
    public Movement save(Movement obj) {
        return null;
    }

    @Override
    public Transaction save(Transaction transaction) {
        return null;
    }
}
