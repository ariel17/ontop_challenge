package ar.com.ariel17.ontop.adapters.repositories;

import ar.com.ariel17.ontop.adapters.repositories.entities.PaymentEntity;
import ar.com.ariel17.ontop.adapters.repositories.entities.PaymentMapper;
import ar.com.ariel17.ontop.adapters.repositories.jpa.JpaPaymentRepository;
import ar.com.ariel17.ontop.core.domain.Payment;
import ar.com.ariel17.ontop.core.repositories.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    @Autowired
    private JpaPaymentRepository repository;

    @Override
    public Payment save(Payment obj) {
        PaymentEntity entity = PaymentMapper.INSTANCE.paymentToPaymentEntity(obj);
        entity = repository.saveAndFlush(entity);
        return PaymentMapper.INSTANCE.paymentEntityToPayment(entity);
    }
}
