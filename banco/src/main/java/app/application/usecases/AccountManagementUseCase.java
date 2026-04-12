package app.application.usecases;

import app.domain.models.BankAccount;
import app.domain.models.AccountType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountManagementUseCase {
    BankAccount openSavingsAccount(String clientId, BigDecimal initialDeposit);
    BankAccount openCheckingAccount(String clientId, BigDecimal initialDeposit);
    BankAccount changeAccountType(String accountId, AccountType newType);
    BigDecimal getBalance(String accountNumber);
    List<BankAccount> findAll();
    Optional<BankAccount> findById(String id);
    List<BankAccount> findByClientId(String clientId);
}
