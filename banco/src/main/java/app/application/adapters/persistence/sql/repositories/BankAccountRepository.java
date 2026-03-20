package app.application.adapters.persistence.sql.repositories;

import app.application.adapters.persistence.sql.entities.BankAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccountEntity, Long> {
  Optional<BankAccountEntity> findByAccountNumber(String accountNumber);
  boolean existsByAccountNumber(String accountNumber);
  List<BankAccountEntity> findByClient_Id(Long clientId);
}
