package ar.com.ariel17.ontop.core.services;

import ar.com.ariel17.ontop.core.domain.BankAccountOwner;
import ar.com.ariel17.ontop.core.repositories.BankAccountOwnerNotFoundException;
import ar.com.ariel17.ontop.core.repositories.BankAccountRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

    @Autowired
    private BankAccountRepository repository;

    @Override
    public void create(@NonNull BankAccountOwner account) {
        repository.save(account);
    }

    @Override
    public BankAccountOwner getByIdAndUserId(Long id, Long userId) throws BankAccountOwnerNotFoundException {
        return repository.getByIdAndUserId(id, userId);
    }
}
