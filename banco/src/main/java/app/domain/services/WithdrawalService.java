package app.domain.services;

import app.domain.Exceptions.*;
import app.domain.models.*;
import app.domain.ports.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service for WITHDRAWAL OPERATIONS
 * Handles: Cash withdrawals from accounts
 */
@Service
public class WithdrawalService {
    
    @Autowired
    private BankAccountRepository accountRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private ValidationService validationService;
    
    @Autowired
    private AuditService auditService;
    
    /**
     * Executes a cash withdrawal from an account
     */
    @Transactional
    public Transaction executeWithdrawal(
            String accountNumber,
            BigDecimal amount,
            String description) {
        
        validationService.validateAmount(amount);
        
        BankAccount account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new BusinessException(
                "Cuenta no encontrada: " + accountNumber));
        
        BigDecimal previousBalance = account.getBalance();
        
        account.debit(amount); // Throws exception if insufficient balance
        accountRepository.save(account);
        
        Transaction transaction = createTransaction(
            account, amount, TransactionType.WITHDRAWAL, description);
        
        Transaction saved = transactionRepository.save(transaction);
        auditService.logTransaction(saved, previousBalance, account.getBalance());
        
        return saved;
    }
    
    private Transaction createTransaction(
            BankAccount account,
            BigDecimal amount,
            TransactionType type,
            String description) {
        
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setTransactionType(type);
        transaction.setDate(LocalDateTime.now());
        transaction.setDescription(
            description != null ? description : "Retiro en efectivo");
        
        return transaction;
    }
}
