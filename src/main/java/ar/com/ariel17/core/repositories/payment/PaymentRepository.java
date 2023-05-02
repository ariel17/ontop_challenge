package ar.com.ariel17.core.repositories.payment;

import ar.com.ariel17.core.domain.Payment;
import ar.com.ariel17.core.repositories.DatabaseRepository;

public interface PaymentRepository extends DatabaseRepository<Payment, PaymentRepositoryException> {
}
