package app.domain.services;

import app.domain.Exceptions.*;
import app.domain.models.*;
import app.domain.ports.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

/**
 * Service for ACCOUNT MANAGEMENT
 * Handles: Account creation, queries, and operations
 */
public class AccountService {

    private final BankAccountPort bankAccountPort;
    private final ClientPort clientPort;
    private final AuditService auditService;

    public AccountService(BankAccountPort bankAccountPort, ClientPort clientPort, AuditService auditService) {
        this.bankAccountPort = bankAccountPort;
        this.clientPort = clientPort;
        this.auditService = auditService;
    }

    // ==================== CREATE OPERATIONS ====================

    /**
     * Opens a new savings account
     */
    public BankAccount openSavingsAccount(Long clientId, BigDecimal initialDeposit) {
        return openAccount(clientId, AccountType.SAVINGS, initialDeposit);
    }

    /**
     * Opens a new checking account
     */
    public BankAccount openCheckingAccount(Long clientId, BigDecimal initialDeposit) {
        return openAccount(clientId, AccountType.CHECKING, initialDeposit);
    }

    private BankAccount openAccount(Long clientId, AccountType type, BigDecimal initialDeposit) {
        PersonClient client = clientPort.findById(clientId);
        if (client == null) {
            throw new UserNotFoundException("Cliente no encontrado: " + clientId);
        }

        validateInitialDeposit(initialDeposit);

        String accountNumber = generateUniqueAccountNumber();

        BankAccount account = new BankAccount();
        account.setId(null);
        account.setAccountNumber(accountNumber);
        account.setAccountType(type);
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setCurrency(Currency.COP);
        account.setCurrentBalance(initialDeposit);
        account.setOpeningDate(LocalDate.now());
        account.setClient(client);

        BankAccount saved = bankAccountPort.save(account);
        auditService.logAccountOpened(saved, initialDeposit);

        return saved;
    }

    // ==================== UPDATE OPERATIONS ====================

    /**
     * Changes account type
     */
    public BankAccount changeAccountType(Long accountId, AccountType newType) {
        BankAccount account = findByIdOrThrow(accountId);

        AccountType previousType = account.getAccountType();
        account.setAccountType(newType);

        BankAccount updated = bankAccountPort.save(account);
        auditService.logAccountTypeChanged(accountId.toString(), previousType, newType);

        return updated;
    }

    // ==================== QUERY OPERATIONS ====================

    /**
     * Gets account balance
     */
    public BigDecimal getBalance(String accountNumber) {
        BankAccount account = findByAccountNumberOrThrow(accountNumber);
        return account.getCurrentBalance();
    }

    public BankAccount findByIdOrThrow(Long id) {
        BankAccount account = bankAccountPort.findById(id);
        if (account == null) {
            throw new BusinessException("Cuenta no encontrada: " + id);
        }
        return account;
    }

    public BankAccount findByAccountNumberOrThrow(String accountNumber) {
        BankAccount account = bankAccountPort.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new BusinessException("Cuenta no encontrada: " + accountNumber);
        }
        return account;
    }

    public BankAccount findByAccountNumber(String accountNumber) {
        return bankAccountPort.findByAccountNumber(accountNumber);
    }

    public BankAccount findById(Long id) {
        return bankAccountPort.findById(id);
    }

    public List<BankAccount> findClientAccounts(Long clientId) {
        return bankAccountPort.findByClientId(clientId);
    }

    public long countClientAccounts(Long clientId) {
        return bankAccountPort.countByClientId(clientId);
    }

    public List<BankAccount> findAll() {
        return bankAccountPort.findAll();
    }

    // ==================== HELPER METHODS ====================

    private void validateInitialDeposit(BigDecimal initialDeposit) {
        if (initialDeposit == null || initialDeposit.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException(
                "Depósito inicial debe ser mayor o igual a 0");
        }
    }

    private String generateUniqueAccountNumber() {
        String accountNumber;
        Random random = new Random();

        do {
            accountNumber = String.format("%010d", random.nextInt(1000000000));
        } while (bankAccountPort.existsByAccountNumber(accountNumber));

        return accountNumber;
    }
}
