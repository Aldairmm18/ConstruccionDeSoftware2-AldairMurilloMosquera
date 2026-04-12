package app.domain.services;

import app.domain.Exceptions.BusinessException;
import app.domain.models.Transaction;
import app.domain.models.TransactionType;
import app.domain.ports.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for TRANSACTION QUERIES
 * Handles: Read-only operations for transaction history
 */
@Service
public class TransactionQueryService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private BankAccountRepository accountRepository;
    
    // ==================== QUERY OPERATIONS ====================
    
    /**
     * Gets all transactions for an account by account number
     */
    public List<Transaction> findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
            .map(account -> transactionRepository.findByAccountId(account.getId()))
            .orElseThrow(() -> new BusinessException(
                "Cuenta no encontrada: " + accountNumber));
    }
    
    /**
     * Gets all transactions for an account by account ID
     */
    public List<Transaction> findByAccountId(String accountId) {
        return transactionRepository.findByAccountId(accountId);
    }
    
    /**
     * Gets transactions by type for an account
     */
    public List<Transaction> findByAccountAndType(
            String accountNumber,
            TransactionType type) {
        
        List<Transaction> allTransactions = findByAccountNumber(accountNumber);
        
        return allTransactions.stream()
            .filter(t -> t.getTransactionType() == type)
            .collect(Collectors.toList());
    }
    
    /**
     * Gets transactions within date range for an account
     */
    public List<Transaction> findByAccountAndDateRange(
            String accountNumber,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        
        List<Transaction> allTransactions = findByAccountNumber(accountNumber);
        
        return allTransactions.stream()
            .filter(t -> !t.getDate().isBefore(startDate) && 
                        !t.getDate().isAfter(endDate))
            .collect(Collectors.toList());
    }
    
    /**
     * Finds transaction by ID
     */
    public Optional<Transaction> findById(String id) {
        return transactionRepository.findById(id);
    }
    
    /**
     * Gets all transactions in the system
     */
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }
    
    /**
     * Counts transactions for an account
     */
    public long countByAccount(String accountNumber) {
        return findByAccountNumber(accountNumber).size();
    }
    
    /**
     * Gets last N transactions for an account
     */
    public List<Transaction> findLastTransactions(String accountNumber, int limit) {
        List<Transaction> allTransactions = findByAccountNumber(accountNumber);
        
        // Sort by date descending
        allTransactions.sort((a, b) -> b.getDate().compareTo(a.getDate()));
        
        return allTransactions.stream()
            .limit(limit)
            .collect(Collectors.toList());
    }
}
