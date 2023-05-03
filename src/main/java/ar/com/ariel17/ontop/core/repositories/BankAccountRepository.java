package ar.com.ariel17.ontop.core.repositories;

import ar.com.ariel17.ontop.core.domain.BankAccountOwner;

import java.util.Optional;

public interface BankAccountRepository extends DatabaseRepository<BankAccountOwner> {

    Optional<BankAccountOwner> getById(Long id);
}
