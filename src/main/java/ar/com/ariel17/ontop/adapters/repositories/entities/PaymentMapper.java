package ar.com.ariel17.ontop.adapters.repositories.entities;

import ar.com.ariel17.ontop.core.domain.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Maps entities and models for Payment providers back and forth.
 */
@Mapper
public interface PaymentMapper {

    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

    Payment paymentEntityToPayment(PaymentEntity entity);

    PaymentEntity paymentToPaymentEntity(Payment payment);
}
