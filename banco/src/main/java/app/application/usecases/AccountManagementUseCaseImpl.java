package app.application.usecases;

import app.domain.Exceptions.BusinessException;
import app.domain.Exceptions.InvalidAmountException;
import app.domain.models.*;
import app.domain.ports.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

// TODO: Delegate business logic to domain services instead of implementing directly here

@Service
@RequiredArgsConstructor
public class AccountManagementUseCaseImpl implements AccountManagementUseCase {

    private final BankAccountPort bankAccountPort;
    private final ClientPort clientPort;
    private final OperationsLogPort operationsLogPort;

    @Override
    @Transactional
    public BankAccount openSavingsAccount(String clientId, BigDecimal initialDeposit) {
        return openAccount(Long.parseLong(clientId), AccountType.SAVINGS, initialDeposit);
    }

    @Override
    @Transactional
    public BankAccount openCheckingAccount(String clientId, BigDecimal initialDeposit) {
        return openAccount(Long.parseLong(clientId), AccountType.CHECKING, initialDeposit);
    }

    private BankAccount openAccount(Long clientId, AccountType type, BigDecimal initialDeposit) {
        PersonClient client = clientPort.findById(clientId);
        if (client == null) {
            throw new BusinessException("Client not found");
        }

        if (initialDeposit == null || initialDeposit.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException("Initial deposit cannot be negative");
        }

        BankAccount account = new BankAccount();
        account.setId(null);
        account.setAccountNumber(generateUniqueAccountNumber());
        account.setAccountType(type);
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setCurrency(Currency.COP);
        account.setCurrentBalance(initialDeposit);
        account.setOpeningDate(LocalDate.now());
        account.setClient(client);

        BankAccount saved = bankAccountPort.save(account);
        registerLog("ACCOUNT_OPENED", saved.getId() != null ? saved.getId().toString() : null);
        return saved;
    }

    @Override
    @Transactional
    public BankAccount changeAccountType(String accountId, AccountType newType) {
        BankAccount account = bankAccountPort.findById(Long.parseLong(accountId));
        if (account == null) {
            throw new BusinessException("Account not found");
        }

        account.setAccountType(newType);
        BankAccount updated = bankAccountPort.save(account);
        registerLog("ACCOUNT_TYPE_CHANGED", accountId);
        return updated;
    }

    @Override
    public BigDecimal getBalance(String accountNumber) {
        BankAccount account = bankAccountPort.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new BusinessException("Account not found");
        }
        return account.getCurrentBalance();
    }

    @Override
    public List<BankAccount> findAll() {
        return bankAccountPort.findAll();
    }

    @Override
    public Optional<BankAccount> findById(String id) {
        return Optional.ofNullable(bankAccountPort.findById(Long.parseLong(id)));
    }

    @Override
    public List<BankAccount> findByClientId(String clientId) {
        return bankAccountPort.findByClientId(Long.parseLong(clientId));
    }

    private String generateUniqueAccountNumber() {
        String num;
        do {
            num = String.format("%010d", new Random().nextInt(1000000000));
        } while (bankAccountPort.existsByAccountNumber(num));
        return num;
    }

    private void registerLog(String operation, String accountId) {
        OperationsLog log = new OperationsLog();
        log.setLogId(UUID.randomUUID().toString());
        log.setOperationType(operation);
        log.setOperationDateTime(java.time.LocalDateTime.now());

        Map<String, Object> details = new HashMap<>();
        details.put("accountId", accountId);
        log.setDetailData(details);

        operationsLogPort.save(log);
    }
}
