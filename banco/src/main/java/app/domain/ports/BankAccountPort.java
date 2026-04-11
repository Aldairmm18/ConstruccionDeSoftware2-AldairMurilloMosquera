package app.domain.ports;

import app.domain.models.BankAccount;
import java.util.List;

public interface BankAccountPort {

  BankAccount save(BankAccount bankAccount);

  BankAccount findById(Long id);

  List<BankAccount> findAll();

  BankAccount findByAccountNumber(String accountNumber);
  BankAccount findByAccountNumberForUpdate(String accountNumber);
  boolean existsByAccountNumber(String accountNumber);
  List<BankAccount> findByClientId(Long clientId);
  long countByClientId(Long clientId);
}
