package ar.com.ariel17.adapters.repositories.recipient;

import ar.com.ariel17.core.domain.bank.BankAccountOwner;
import ar.com.ariel17.core.repositories.recipient.RecipientRepository;
import ar.com.ariel17.core.repositories.recipient.RecipientRepositoryException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecipientRepositoryImpl implements RecipientRepository {

    @Autowired
    private JpaRecipientRepository jpaRepository;

    @Override
    public void save(@NonNull BankAccountOwner obj) throws RecipientRepositoryException {
        BankAccountOwnerEntity entity = BankAccountOwnerMapper.INSTANCE.bankAccountOwnerToBankAccountOwnerEntity(obj);
        jpaRepository.save(entity);
    }
}
