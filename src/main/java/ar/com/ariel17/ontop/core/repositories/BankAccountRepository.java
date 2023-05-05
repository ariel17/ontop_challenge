package ar.com.ariel17.ontop.core.repositories;

import ar.com.ariel17.ontop.core.domain.BankAccountOwner;

public interface BankAccountRepository extends DatabaseRepository<BankAccountOwner> {

    BankAccountOwner getById(Long id) throws BankAccountOwnerNotFoundException;
    // TODO ontop account should be saved too
}
