package app.domain.ports;

import app.domain.models.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, String> {
    Optional<BankAccount> findByAccountNumber(String accountNumber);
    List<BankAccount> findByClientId(String clientId);
    long countByClientId(String clientId);
}
