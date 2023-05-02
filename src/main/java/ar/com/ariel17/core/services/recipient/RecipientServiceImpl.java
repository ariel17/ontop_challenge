package ar.com.ariel17.core.services.recipient;

import ar.com.ariel17.core.domain.bank.BankAccountOwner;
import ar.com.ariel17.core.repositories.recipient.RecipientRepository;
import ar.com.ariel17.core.repositories.recipient.RecipientRepositoryException;
import org.jetbrains.annotations.NotNull;

public class RecipientServiceImpl implements RecipientService {

    private final RecipientRepository repository;

    public RecipientServiceImpl(@NotNull final RecipientRepository repository) {
        this.repository = repository;
    }

    @Override
    public void createRecipient(@NotNull BankAccountOwner recipient) throws RecipientException {
        try {
            repository.save(recipient);
        } catch (RecipientRepositoryException e) {
            throw new RecipientException(e);
        }
    }
}
