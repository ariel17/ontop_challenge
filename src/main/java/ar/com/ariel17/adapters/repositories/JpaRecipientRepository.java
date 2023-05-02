package ar.com.ariel17.adapters.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaRecipientRepository extends JpaRepository<BankAccountOwnerEntity, Long> {
}
