package ar.com.ariel17.core.services.recipient;

import ar.com.ariel17.core.domain.bank.BankAccountOwner;
import org.springframework.stereotype.Service;

/**
 * RecipientService provides the contract for implementators on recipient
 * management.
 */
@Service
public interface RecipientService {

    /**
     * Creates a new recipient in the repository, if not exists.
     *
     * @param recipient The recipient to create.
     */
    void createRecipient(BankAccountOwner recipient) throws RecipientException;
}
