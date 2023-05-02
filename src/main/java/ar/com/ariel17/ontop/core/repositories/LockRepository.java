package ar.com.ariel17.ontop.core.repositories;

/**
 * LockRepository is the contract for lock mechanism repository.
 */
public interface LockRepository extends AutoCloseable {

    /**
     * Acquires a new lock.
     *
     * @return The lock operation result.
     */
    boolean acquire();
}
