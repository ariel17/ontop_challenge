package ar.com.ariel17.ontop.adapters.repositories;

import ar.com.ariel17.ontop.adapters.repositories.entities.BankAccountOwnerEntity;
import ar.com.ariel17.ontop.adapters.repositories.entities.BankAccountOwnerMapper;
import ar.com.ariel17.ontop.adapters.repositories.jpa.JpaBankAccountRepository;
import ar.com.ariel17.ontop.core.domain.BankAccountOwner;
import ar.com.ariel17.ontop.core.repositories.BankAccountOwnerNotFoundException;
import ar.com.ariel17.ontop.core.repositories.BankAccountRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class BankAccountRepositoryImpl implements BankAccountRepository {

    @Autowired
    private JpaBankAccountRepository jpaRepository;

    @Override
    public BankAccountOwner save(@NonNull BankAccountOwner obj) {
        BankAccountOwnerEntity entity = BankAccountOwnerMapper.INSTANCE.bankAccountOwnerToBankAccountOwnerEntity(obj);
        entity = jpaRepository.saveAndFlush(entity);
        return BankAccountOwnerMapper.INSTANCE.bankAccountOwnerEntityToBankAccountOwner(entity);
    }

    @Override
    public BankAccountOwner getById(Long id) throws BankAccountOwnerNotFoundException {
        Optional<BankAccountOwnerEntity> o = jpaRepository.findById(id);
        if (o.isEmpty()) {
            throw new BankAccountOwnerNotFoundException(String.format("Bank account owner not found for id=%d", id));
        }
        BankAccountOwnerEntity entity = o.get();
        return BankAccountOwnerMapper.INSTANCE.bankAccountOwnerEntityToBankAccountOwner(entity);
    }
}
