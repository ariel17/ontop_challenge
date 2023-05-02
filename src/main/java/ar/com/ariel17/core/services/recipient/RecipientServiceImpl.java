package ar.com.ariel17.core.services.recipient;

import ar.com.ariel17.core.domain.bank.BankAccountOwner;
import ar.com.ariel17.core.repositories.recipient.RecipientRepository;
import ar.com.ariel17.core.repositories.recipient.RecipientRepositoryException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecipientServiceImpl implements RecipientService {

    @Autowired
    private RecipientRepository repository;

    public RecipientServiceImpl(RecipientRepository repository) {
        this.repository = repository;
    }

    @Override
    public void createRecipient(@NonNull BankAccountOwner recipient) throws RecipientException{
        try {
            repository.save(recipient);
        } catch (RecipientRepositoryException e) {
            throw new RecipientException(e);
        }
    }
}
