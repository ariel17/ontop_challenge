package ar.com.ariel17.core.services;

import ar.com.ariel17.core.domain.BankAccountOwner;
import ar.com.ariel17.core.repositories.RecipientRepository;
import ar.com.ariel17.core.repositories.RecipientRepositoryException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RecipientServiceImpl implements RecipientService {

    @Autowired
    private RecipientRepository repository;

    @Override
    public void createRecipient(@NonNull BankAccountOwner recipient) throws RecipientException {
        try {
            repository.save(recipient);
        } catch (RecipientRepositoryException e) {
            throw new RecipientException(e);
        }
    }
}
