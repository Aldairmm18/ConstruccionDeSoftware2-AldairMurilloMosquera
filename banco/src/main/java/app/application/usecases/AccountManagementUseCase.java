package app.application.usecases;

import app.domain.models.BankAccount;
import java.util.List;

public interface AccountManagementUseCase {
    BankAccount createAccount(BankAccount account);
    BankAccount blockAccount(Long accountId);
    List<BankAccount> findAll();
    BankAccount findById(Long id);
    List<BankAccount> findByClientId(Long clientId);
}
