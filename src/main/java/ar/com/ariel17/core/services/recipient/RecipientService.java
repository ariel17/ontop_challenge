package ar.com.ariel17.core.services.recipient;

import ar.com.ariel17.core.domain.bank.BankAccountOwner;
import org.jetbrains.annotations.NotNull;

/**
 * RecipientService provides the contract for implementators on recipient
 * management.
 */
public interface RecipientService {

    /**
     * Creates a new recipient in the repository, if not exists.
     *
     * @param recipient The recipient to create.
     * @throws RecipientException Thrown when the recipient is not valid.
     */
    void createRecipient(@NotNull BankAccountOwner recipient) throws RecipientException;
}
