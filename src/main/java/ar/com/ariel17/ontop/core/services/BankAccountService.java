package ar.com.ariel17.ontop.core.services;

import ar.com.ariel17.ontop.core.domain.BankAccountOwner;
import org.springframework.stereotype.Service;

/**
 * BankAccountService provides the contract for implementators on bank account
 * management.
 */
@Service
public interface BankAccountService {

    /**
     * Creates a new bank account in the repository, if not exists.
     *
     * @param account The recipient to create.
     */
    void create(BankAccountOwner account) throws BankAccountException;
}
