package ar.com.ariel17.ontop.adapters.repositories.entities;

import ar.com.ariel17.ontop.core.domain.Movement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MovementMapper {

    MovementMapper INSTANCE = Mappers.getMapper(MovementMapper.class);

    @Mapping(source = "entity.fromRouting", target = "from.routing")
    @Mapping(source = "entity.fromAccount", target = "from.account")
    @Mapping(source = "entity.currency", target = "from.currency")
    @Mapping(source = "entity.toRouting", target = "to.routing")
    @Mapping(source = "entity.toAccount", target = "to.account")
    @Mapping(source = "entity.currency", target = "to.currency")
    Movement movementEntityToMovement(MovementEntity entity);

    @Mapping(source = "movement.from.routing", target = "fromRouting")
    @Mapping(source = "movement.from.account", target = "fromAccount")
    @Mapping(source = "movement.from.currency", target = "currency")
    @Mapping(source = "movement.to.routing", target = "toRouting")
    @Mapping(source = "movement.to.account", target = "toAccount")
    MovementEntity movementToMovementEntity(Movement movement);

    List<MovementEntity> movementsToMovementEntities(List<Movement> movements);

    List<Movement> movementEntitiesToMovements(List<MovementEntity> entities);
}
