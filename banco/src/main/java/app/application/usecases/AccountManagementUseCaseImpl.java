package app.application.usecases;

import app.domain.models.AccountType;
import app.domain.models.BankAccount;
import app.domain.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountManagementUseCaseImpl implements AccountManagementUseCase {

    private final AccountService accountService;

    @Override
    @Transactional
    public BankAccount openSavingsAccount(String clientId, BigDecimal initialDeposit) {
        return accountService.openSavingsAccount(Long.parseLong(clientId), initialDeposit);
    }

    @Override
    @Transactional
    public BankAccount openCheckingAccount(String clientId, BigDecimal initialDeposit) {
        return accountService.openCheckingAccount(Long.parseLong(clientId), initialDeposit);
    }

    @Override
    @Transactional
    public BankAccount changeAccountType(String accountId, AccountType newType) {
        return accountService.changeAccountType(Long.parseLong(accountId), newType);
    }

    @Override
    public BigDecimal getBalance(String accountNumber) {
        return accountService.getBalance(accountNumber);
    }

    @Override
    public List<BankAccount> findAll() {
        return accountService.findAll();
    }

    @Override
    public Optional<BankAccount> findById(String id) {
        return Optional.ofNullable(accountService.findById(Long.parseLong(id)));
    }

    @Override
    public List<BankAccount> findByClientId(String clientId) {
        return accountService.findClientAccounts(Long.parseLong(clientId));
    }
}
