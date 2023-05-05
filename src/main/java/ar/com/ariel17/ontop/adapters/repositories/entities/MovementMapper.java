package ar.com.ariel17.ontop.adapters.repositories.entities;

import ar.com.ariel17.ontop.core.domain.Movement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MovementMapper {

    MovementMapper INSTANCE = Mappers.getMapper(MovementMapper.class);

    @Mapping(source = "entity.onTopAccount.id", target = "onTopAccount.id")
    @Mapping(source = "entity.onTopAccount.userId", target = "onTopAccount.userId")
    @Mapping(source = "entity.onTopAccount.routing", target = "onTopAccount.bankAccount.routing")
    @Mapping(source = "entity.onTopAccount.account", target = "onTopAccount.bankAccount.account")
    @Mapping(source = "entity.onTopAccount.type", target = "onTopAccount.bankAccount.type")
    @Mapping(source = "entity.onTopAccount.currency", target = "onTopAccount.bankAccount.currency")
    @Mapping(source = "entity.onTopAccount.idNumber", target = "onTopAccount.idNumber")
    @Mapping(source = "entity.onTopAccount.firstName", target = "onTopAccount.firstName")
    @Mapping(source = "entity.onTopAccount.lastName", target = "onTopAccount.lastName")
    @Mapping(source = "entity.onTopAccount.createdAt", target = "onTopAccount.createdAt")
    @Mapping(source = "entity.externalAccount.id", target = "externalAccount.id")
    @Mapping(source = "entity.externalAccount.userId", target = "externalAccount.userId")
    @Mapping(source = "entity.externalAccount.routing", target = "externalAccount.bankAccount.routing")
    @Mapping(source = "entity.externalAccount.account", target = "externalAccount.bankAccount.account")
    @Mapping(source = "entity.externalAccount.type", target = "externalAccount.bankAccount.type")
    @Mapping(source = "entity.externalAccount.currency", target = "externalAccount.bankAccount.currency")
    @Mapping(source = "entity.externalAccount.idNumber", target = "externalAccount.idNumber")
    @Mapping(source = "entity.externalAccount.firstName", target = "externalAccount.firstName")
    @Mapping(source = "entity.externalAccount.lastName", target = "externalAccount.lastName")
    @Mapping(source = "entity.externalAccount.createdAt", target = "externalAccount.createdAt")
    Movement movementEntityToMovement(MovementEntity entity);

    @Mapping(source = "movement.onTopAccount.id", target = "onTopAccount.id")
    @Mapping(source = "movement.onTopAccount.userId", target = "onTopAccount.userId")
    @Mapping(source = "movement.onTopAccount.bankAccount.routing", target = "onTopAccount.routing")
    @Mapping(source = "movement.onTopAccount.bankAccount.account", target = "onTopAccount.account")
    @Mapping(source = "movement.onTopAccount.bankAccount.type", target = "onTopAccount.type")
    @Mapping(source = "movement.onTopAccount.bankAccount.currency", target = "onTopAccount.currency")
    @Mapping(source = "movement.onTopAccount.idNumber", target = "onTopAccount.idNumber")
    @Mapping(source = "movement.onTopAccount.firstName", target = "onTopAccount.firstName")
    @Mapping(source = "movement.onTopAccount.lastName", target = "onTopAccount.lastName")
    @Mapping(source = "movement.onTopAccount.createdAt", target = "onTopAccount.createdAt")
    @Mapping(source = "movement.externalAccount.id", target = "externalAccount.id")
    @Mapping(source = "movement.externalAccount.userId", target = "externalAccount.userId")
    @Mapping(source = "movement.externalAccount.bankAccount.routing", target = "externalAccount.routing")
    @Mapping(source = "movement.externalAccount.bankAccount.account", target = "externalAccount.account")
    @Mapping(source = "movement.externalAccount.bankAccount.type", target = "externalAccount.type")
    @Mapping(source = "movement.externalAccount.bankAccount.currency", target = "externalAccount.currency")
    @Mapping(source = "movement.externalAccount.idNumber", target = "externalAccount.idNumber")
    @Mapping(source = "movement.externalAccount.firstName", target = "externalAccount.firstName")
    @Mapping(source = "movement.externalAccount.lastName", target = "externalAccount.lastName")
    @Mapping(source = "movement.externalAccount.createdAt", target = "externalAccount.createdAt")
    MovementEntity movementToMovementEntity(Movement movement);

    List<MovementEntity> movementsToMovementEntities(List<Movement> movements);

    List<Movement> movementEntitiesToMovements(List<MovementEntity> entities);
}
