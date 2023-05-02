package ar.com.ariel17.ontop.adapters.repositories.jpa;

import ar.com.ariel17.ontop.adapters.repositories.entities.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaPaymentRepository extends JpaRepository<PaymentEntity, UUID> {
}
