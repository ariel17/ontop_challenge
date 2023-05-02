package ar.com.ariel17.ontop.adapters.repositories;

import ar.com.ariel17.ontop.core.domain.BankAccountOwner;
import ar.com.ariel17.ontop.core.repositories.BankAccountRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BankAccountRepositoryImpl implements BankAccountRepository {

    @Autowired
    private JpaBankAccountRepository jpaRepository;

    @Override
    public BankAccountOwner save(@NonNull BankAccountOwner obj) {
        BankAccountOwnerEntity entity = BankAccountOwnerMapper.INSTANCE.bankAccountOwnerToBankAccountOwnerEntity(obj);
        entity = jpaRepository.save(entity);
        return BankAccountOwnerMapper.INSTANCE.bankAccountOwnerEntityToBankAccountOwner(entity);
    }
}
