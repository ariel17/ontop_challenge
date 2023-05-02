package ar.com.ariel17.adapters.repositories.payment;

import ar.com.ariel17.core.domain.Payment;
import ar.com.ariel17.core.repositories.payment.PaymentRepository;
import ar.com.ariel17.core.repositories.payment.PaymentRepositoryException;
import org.springframework.stereotype.Component;

@Component
public class PaymentRepositoryImpl implements PaymentRepository {

    @Override
    public Payment save(Payment obj) throws PaymentRepositoryException {
        return null;
    }
}
