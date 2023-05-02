package ar.com.ariel17.adapters.repositories;

import ar.com.ariel17.core.domain.BankAccountOwner;
import ar.com.ariel17.core.repositories.RecipientRepository;
import ar.com.ariel17.core.repositories.RecipientRepositoryException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecipientRepositoryImpl implements RecipientRepository {

    @Autowired
    private JpaRecipientRepository jpaRepository;

    @Override
    public BankAccountOwner save(@NonNull BankAccountOwner obj) throws RecipientRepositoryException {
        BankAccountOwnerEntity entity = BankAccountOwnerMapper.INSTANCE.bankAccountOwnerToBankAccountOwnerEntity(obj);
        entity = jpaRepository.save(entity);
        return BankAccountOwnerMapper.INSTANCE.bankAccountOwnerEntityToBankAccountOwner(entity);
    }
}
