package ar.com.ariel17.adapters.repositories.movement;

import ar.com.ariel17.core.domain.transaction.Movement;
import ar.com.ariel17.core.repositories.movement.MovementRepository;
import ar.com.ariel17.core.repositories.movement.MovementRepositoryException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovementRepositoryImpl implements MovementRepository {

    @Override
    public void save(Movement obj) throws MovementRepositoryException {

    }

    @Override
    public void save(List<Movement> movements) throws MovementRepositoryException {

    }
}
