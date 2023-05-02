package ar.com.ariel17.ontop.adapters.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.integration.support.locks.ExpirableLockRegistry;

import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LockRepositoryImplTest {

    @Mock
    private ExpirableLockRegistry registry;

    @Mock
    private Lock lock;

    private Long userId;

    private LockRepositoryImpl repository;

    @BeforeEach
    public void setUp() {
        userId = 10L;
        repository = new LockRepositoryImpl(registry, userId);
    }

    @Test
    public void testAcquire_ok() {
        when(lock.tryLock()).thenReturn(true);
        when(registry.obtain(eq("user_id-10"))).thenReturn(lock);
        assertTrue(repository.acquire());
        verify(lock, times(1)).tryLock();
    }

    @Test
    public void testAcquire_failed() {
        when(lock.tryLock()).thenReturn(false);
        when(registry.obtain(eq("user_id-10"))).thenReturn(lock);
        assertFalse(repository.acquire());
        verify(lock, times(1)).tryLock();
    }

    @Test
    public void testClose_ok() throws Exception {
        when(lock.tryLock()).thenReturn(true);
        when(registry.obtain(eq("user_id-10"))).thenReturn(lock);
        repository.acquire();
        repository.close();
        verify(lock, times(1)).tryLock();
        verify(lock, times(1)).unlock();
    }

}
