package ar.com.ariel17.ontop.core.services;

import ar.com.ariel17.ontop.core.domain.BankAccountOwner;
import ar.com.ariel17.ontop.core.repositories.RecipientRepository;
import ar.com.ariel17.ontop.core.repositories.RecipientRepositoryException;
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
