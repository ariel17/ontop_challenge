package ar.com.ariel17.ontop.adapters.repositories;

import ar.com.ariel17.ontop.adapters.repositories.entities.PaymentEntity;
import ar.com.ariel17.ontop.adapters.repositories.jpa.JpaPaymentRepository;
import ar.com.ariel17.ontop.core.domain.Payment;
import ar.com.ariel17.ontop.core.domain.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentRepositoryImplTest {

    @Mock
    private JpaPaymentRepository jpaRepository;

    private PaymentRepositoryImpl repository;

    private Payment payment;

    @BeforeEach
    public void setUp() {
        repository = new PaymentRepositoryImpl(jpaRepository);
        payment = new Payment(null, new BigDecimal(1000), PaymentStatus.FAILED, "oh my god what we've done", null);
    }

    @Test
    public void testSave_ok() {
        repository.save(payment);
        verify(jpaRepository, times(1)).saveAndFlush(any(PaymentEntity.class));
    }
}
