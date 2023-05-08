package ar.com.ariel17.ontop.adapters.repositories.entities;

import ar.com.ariel17.ontop.core.domain.BankAccountOwner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Back account owner entity and model mapper, back and forth.
 */
@Mapper
public interface BankAccountOwnerMapper {

    BankAccountOwnerMapper INSTANCE = Mappers.getMapper(BankAccountOwnerMapper.class);

    @Mapping(source = "entity.routing", target = "bankAccount.routing")
    @Mapping(source = "entity.account", target = "bankAccount.account")
    @Mapping(source = "entity.type", target = "bankAccount.type")
    @Mapping(source = "entity.currency", target = "bankAccount.currency")
    BankAccountOwner bankAccountOwnerEntityToBankAccountOwner(BankAccountOwnerEntity entity);

    @Mapping(source = "owner.bankAccount.routing", target = "routing")
    @Mapping(source = "owner.bankAccount.account", target = "account")
    @Mapping(source = "owner.bankAccount.type", target = "type")
    @Mapping(source = "owner.bankAccount.currency", target = "currency")
    BankAccountOwnerEntity bankAccountOwnerToBankAccountOwnerEntity(BankAccountOwner owner);
}
