package app.application.usecases;

import app.domain.Exceptions.BusinessException;
import app.domain.Exceptions.InvalidAmountException;
import app.domain.models.*;
import app.domain.ports.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AccountManagementUseCaseImpl implements AccountManagementUseCase {

    private final BankAccountRepository bankAccountRepository;
    private final ClientRepository clientRepository;
    private final OperationsLogRepository operationsLogRepository;

    @Override
    @Transactional
    public BankAccount openSavingsAccount(String clientId, BigDecimal initialDeposit) {
        return openAccount(clientId, AccountType.SAVINGS, initialDeposit);
    }

    @Override
    @Transactional
    public BankAccount openCheckingAccount(String clientId, BigDecimal initialDeposit) {
        return openAccount(clientId, AccountType.CHECKING, initialDeposit);
    }

    private BankAccount openAccount(String clientId, AccountType type, BigDecimal initialDeposit) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new BusinessException("Client not found"));

        if (initialDeposit == null || initialDeposit.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException("Initial deposit cannot be negative");
        }

        BankAccount account = new BankAccount();
        account.setId(UUID.randomUUID().toString());
        account.setAccountNumber(generateUniqueAccountNumber());
        account.setAccountType(type);
        account.setBalance(initialDeposit);
        account.setCreatedAt(LocalDateTime.now());
        account.setClient(client);

        BankAccount saved = bankAccountRepository.save(account);
        registerLog("ACCOUNT_OPENED", saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public BankAccount changeAccountType(String accountId, AccountType newType) {
        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException("Account not found"));
        
        account.setAccountType(newType);
        BankAccount updated = bankAccountRepository.save(account);
        registerLog("ACCOUNT_TYPE_CHANGED", accountId);
        return updated;
    }

    @Override
    public BigDecimal getBalance(String accountNumber) {
        return bankAccountRepository.findByAccountNumber(accountNumber)
                .map(BankAccount::getBalance)
                .orElseThrow(() -> new BusinessException("Account not found"));
    }

    @Override
    public List<BankAccount> findAll() {
        return bankAccountRepository.findAll();
    }

    @Override
    public Optional<BankAccount> findById(String id) {
        return bankAccountRepository.findById(id);
    }

    @Override
    public List<BankAccount> findByClientId(String clientId) {
        return bankAccountRepository.findByClientId(clientId);
    }

    private String generateUniqueAccountNumber() {
        String num;
        do {
            num = String.format("%010d", new Random().nextInt(1000000000));
        } while (bankAccountRepository.findByAccountNumber(num).isPresent());
        return num;
    }

    private void registerLog(String operation, String accountId) {
        OperationsLog log = new OperationsLog();
        log.setId(UUID.randomUUID().toString());
        log.setTimestamp(LocalDateTime.now());
        log.setOperation(operation);
        
        Map<String, String> details = new HashMap<>();
        details.put("accountId", accountId);
        log.setDetails(details);
        
        operationsLogRepository.save(log);
    }
}
