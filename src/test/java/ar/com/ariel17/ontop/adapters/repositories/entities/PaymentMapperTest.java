package ar.com.ariel17.ontop.adapters.repositories.entities;

import ar.com.ariel17.ontop.core.domain.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PaymentMapperTest {

    private Payment payment;

    @BeforeEach
    public void setUp() {
        payment = Payment.builder().
                id(UUID.randomUUID()).
                amount(new BigDecimal("1000.01")).
                status("error").
                error("This is an error description").
                createdAt(new Date()).
                build();
    }

    @Test
    public void testPaymentToPaymentEntity() {
        PaymentEntity entity = PaymentMapper.INSTANCE.paymentToPaymentEntity(payment);
        compare(payment, entity);
    }

    @Test
    public void testPaymentEntityToPayment() {
        PaymentEntity entity = PaymentMapper.INSTANCE.paymentToPaymentEntity(payment);
        Payment payment2 = PaymentMapper.INSTANCE.paymentEntityToPayment(entity);
        compare(payment2, entity);
    }

    private void compare(Payment payment, PaymentEntity entity) {
        assertNotNull(payment.getId());
        assertNotNull(entity.getId());
        assertEquals(payment.getId(), entity.getId());

        assertNotNull(payment.getAmount());
        assertNotNull(entity.getAmount());
        assertEquals(payment.getAmount(), entity.getAmount());

        assertNotNull(payment.getStatus());
        assertNotNull(entity.getStatus());
        assertEquals(payment.getStatus(), entity.getStatus());

        assertNotNull(payment.getError());
        assertNotNull(entity.getError());
        assertEquals(payment.getError(), entity.getError());

        assertNotNull(payment.getCreatedAt());
        assertNotNull(entity.getCreatedAt());
        assertEquals(payment.getCreatedAt(), entity.getCreatedAt());
    }
}
