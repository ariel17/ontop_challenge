package ar.com.ariel17.core.repositories;

import ar.com.ariel17.core.repositories.movement.MovementRepositoryException;

/**
 * LockRepository is the contract for lock mechanism repository.
 */
public interface LockRepository extends AutoCloseable {

    /**
     * Acquires a new lock.
     *
     * @return The lock operation result.
     * @throws MovementRepositoryException Thrown when the write operation fails.
     */
    boolean acquire() throws MovementRepositoryException;
}
