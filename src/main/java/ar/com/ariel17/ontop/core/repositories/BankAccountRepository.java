package ar.com.ariel17.ontop.core.repositories;

import ar.com.ariel17.ontop.core.domain.BankAccountOwner;

/**
 * Bank account owner repository contract.
 */
public interface BankAccountRepository extends DatabaseRepository<BankAccountOwner> {

    BankAccountOwner getByIdAndUserId(Long id, Long userId) throws BankAccountOwnerNotFoundException;
}
