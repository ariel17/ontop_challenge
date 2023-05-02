package ar.com.ariel17.ontop.adapters.repositories;

import ar.com.ariel17.ontop.core.repositories.LockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.support.locks.ExpirableLockRegistry;

import java.util.concurrent.locks.Lock;

@RequiredArgsConstructor
public class LockRepositoryImpl implements LockRepository {

    private static final String KEY_FORMAT = "user_id-%d";

    private final ExpirableLockRegistry lockRegistry;

    private final Long userId;

    private Lock lock;

    @Override
    public boolean acquire() {
        if (lock == null) {
            lock = lockRegistry.obtain(getLockKey());
        }
        return lock.tryLock();
    }

    @Override
    public void close() throws Exception {
        if (lock != null) {
            lock.unlock();
        }
    }

    protected String getLockKey() {
        return String.format(KEY_FORMAT, userId);
    }
}
