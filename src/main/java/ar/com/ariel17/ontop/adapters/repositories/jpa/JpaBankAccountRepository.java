package ar.com.ariel17.ontop.adapters.repositories.jpa;

import ar.com.ariel17.ontop.adapters.repositories.entities.BankAccountOwnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaBankAccountRepository extends JpaRepository<BankAccountOwnerEntity, Long> {
}
