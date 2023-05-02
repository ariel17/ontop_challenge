package ar.com.ariel17.ontop.core.repositories;

/**
 * Repository contract for database implementations.
 *
 * @param <T> Object type to store.
 */
public interface DatabaseRepository<T, E extends Throwable> {

    /**
     * Saves the object into database as record.
     *
     * @param obj The object to store.
     * @throws E Raised when the operation fails.
     */
    T save(T obj) throws E;
}
