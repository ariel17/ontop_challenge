package ar.com.ariel17.core.services.lock;

import ar.com.ariel17.core.repositories.LockRepository;
import org.springframework.stereotype.Service;

@Service
public class LockServiceImpl implements LockService {

    @Override
    public LockRepository createLockForUserId(Long userId) {
        // TODO added by force :(
        return null;
    }
}
