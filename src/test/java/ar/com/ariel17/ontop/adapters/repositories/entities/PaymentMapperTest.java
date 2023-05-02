package ar.com.ariel17.ontop.adapters.repositories.entities;

import ar.com.ariel17.ontop.core.domain.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PaymentMapperTest {

    private Payment payment;

    @BeforeEach
    public void setUp() {
        payment = new Payment(UUID.randomUUID(), new BigDecimal("1000.01"), "error", "This is an error description", new Date());
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
        assertEquals(payment.getId(), entity.getId());
        assertEquals(payment.getAmount(), entity.getAmount());
        assertEquals(payment.getStatus(), entity.getStatus());
        assertEquals(payment.getError(), entity.getError());
        assertEquals(payment.getCreatedAt(), entity.getCreatedAt());
    }
}
