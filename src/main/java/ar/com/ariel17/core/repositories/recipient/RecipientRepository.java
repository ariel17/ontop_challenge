package ar.com.ariel17.core.repositories.recipient;

import ar.com.ariel17.core.domain.bank.BankAccountOwner;
import ar.com.ariel17.core.repositories.DatabaseRepository;

public interface RecipientRepository extends DatabaseRepository<BankAccountOwner, RecipientRepositoryException> {
}
