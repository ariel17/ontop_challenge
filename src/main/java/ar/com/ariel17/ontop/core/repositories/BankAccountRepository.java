package ar.com.ariel17.ontop.core.repositories;

import ar.com.ariel17.ontop.core.domain.BankAccountOwner;

public interface BankAccountRepository extends DatabaseRepository<BankAccountOwner> {

    BankAccountOwner getById(Long id) throws BankAccountOwnerNotFoundException;
    // TODO add contraint for only 1 routing+account+user
    // TODO ontop account should be saved too
    // TODO change the movement model to support ontop_account_id + external_account_id
    // TODO add config for ontop.user_id = 0
}
