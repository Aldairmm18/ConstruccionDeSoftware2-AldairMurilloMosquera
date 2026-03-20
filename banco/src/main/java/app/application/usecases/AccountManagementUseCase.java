package app.application.usecases;

import app.domain.models.BankAccount;

public interface AccountManagementUseCase {
    BankAccount createAccount(BankAccount account);
    BankAccount blockAccount(Long accountId);
}
