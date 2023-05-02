package ar.com.ariel17.core.services;

import ar.com.ariel17.core.repositories.LockRepository;
import org.springframework.stereotype.Service;

/**
 * LockService helps to create specific user ID related locks.
 */
@Service
public interface LockService {

    /**
     * Creates a lock repository for the specific user ID.
     *
     * @param userId The user ID to restrict locks to.
     * @return The lock repository.
     */
    LockRepository createLockForUserId(Long userId);
}
