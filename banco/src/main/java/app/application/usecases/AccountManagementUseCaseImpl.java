package app.application.usecases;

import app.domain.models.BankAccount;
import app.domain.models.AccountStatus;
import app.domain.ports.BankAccountPort;
import app.domain.services.BankAccountDomainService;
import app.domain.Exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountManagementUseCaseImpl implements AccountManagementUseCase {

    private final BankAccountPort bankAccountPort;
    private final BankAccountDomainService bankAccountDomainService;

    @Override
    public BankAccount createAccount(BankAccount account) {
        bankAccountDomainService.validateBankAccountCreation(account);
        return bankAccountPort.save(account);
    }

    @Override
    public BankAccount blockAccount(Long accountId) {
        BankAccount account = bankAccountPort.findById(accountId);
        if (account == null) {
            throw new BusinessException("Cuenta no encontrada.");
        }
        account.setAccountStatus(AccountStatus.BLOCKED);
        return bankAccountPort.save(account);
    }
}
