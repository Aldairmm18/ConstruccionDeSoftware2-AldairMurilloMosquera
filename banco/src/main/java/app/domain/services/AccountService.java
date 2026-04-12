package app.domain.services;

import app.domain.Exceptions.*;
import app.domain.models.*;
import app.domain.ports.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

/**
 * Service for ACCOUNT MANAGEMENT
 * Handles: Account creation, queries, and operations
 */
@Service
public class AccountService {
    
    @Autowired
    private BankAccountRepository accountRepository;
    
    @Autowired
    private ClientRepository clientRepository;
    
    @Autowired
    private AuditService auditService;
    
    // ==================== CREATE OPERATIONS ====================
    
    /**
     * Opens a new savings account
     */
    @Transactional
    public BankAccount openSavingsAccount(String clientId, BigDecimal initialDeposit) {
        return openAccount(clientId, AccountType.SAVINGS, initialDeposit);
    }
    
    /**
     * Opens a new checking account
     */
    @Transactional
    public BankAccount openCheckingAccount(String clientId, BigDecimal initialDeposit) {
        return openAccount(clientId, AccountType.CHECKING, initialDeposit);
    }
    
    private BankAccount openAccount(String clientId, AccountType type, BigDecimal initialDeposit) {
        Client client = clientRepository.findById(clientId)
            .orElseThrow(() -> new UserNotFoundException(
                "Cliente no encontrado: " + clientId));
        
        validateInitialDeposit(initialDeposit);
        
        String accountNumber = generateUniqueAccountNumber();
        
        BankAccount account = new BankAccount();
        account.setId(UUID.randomUUID().toString());
        account.setAccountNumber(accountNumber);
        account.setAccountType(type);
        account.setBalance(initialDeposit);
        account.setCreatedAt(LocalDateTime.now());
        account.setClient(client);
        
        BankAccount saved = accountRepository.save(account);
        auditService.logAccountOpened(saved, initialDeposit);
        
        return saved;
    }
    
    // ==================== UPDATE OPERATIONS ====================
    
    /**
     * Changes account type
     */
    @Transactional
    public BankAccount changeAccountType(String accountId, AccountType newType) {
        BankAccount account = findByIdOrThrow(accountId);
        
        AccountType previousType = account.getAccountType();
        account.setAccountType(newType);
        
        BankAccount updated = accountRepository.save(account);
        auditService.logAccountTypeChanged(accountId, previousType, newType);
        
        return updated;
    }
    
    // ==================== QUERY OPERATIONS ====================
    
    /**
     * Gets account balance
     */
    public BigDecimal getBalance(String accountNumber) {
        BankAccount account = findByAccountNumberOrThrow(accountNumber);
        return account.getBalance();
    }
    
    public BankAccount findByIdOrThrow(String id) {
        return accountRepository.findById(id)
            .orElseThrow(() -> new BusinessException(
                "Cuenta no encontrada: " + id));
    }
    
    public BankAccount findByAccountNumberOrThrow(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new BusinessException(
                "Cuenta no encontrada: " + accountNumber));
    }
    
    public Optional<BankAccount> findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }
    
    public Optional<BankAccount> findById(String id) {
        return accountRepository.findById(id);
    }
    
    public List<BankAccount> findClientAccounts(String clientId) {
        return accountRepository.findByClientId(clientId);
    }
    
    public long countClientAccounts(String clientId) {
        return accountRepository.countByClientId(clientId);
    }
    
    public List<BankAccount> findAll() {
        return accountRepository.findAll();
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
        } while (accountRepository.findByAccountNumber(accountNumber).isPresent());
        
        return accountNumber;
    }
}
