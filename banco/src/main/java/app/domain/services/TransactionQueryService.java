package app.domain.services;

import app.domain.Exceptions.BusinessException;
import app.domain.models.Transaction;
import app.domain.models.TransactionType;
import app.domain.models.BankAccount;
import app.domain.ports.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for TRANSACTION QUERIES
 * Handles: Read-only operations for transaction history
 */
public class TransactionQueryService {

    private final TransactionPort transactionPort;
    private final BankAccountPort bankAccountPort;

    public TransactionQueryService(TransactionPort transactionPort, BankAccountPort bankAccountPort) {
        this.transactionPort = transactionPort;
        this.bankAccountPort = bankAccountPort;
    }

    // ==================== QUERY OPERATIONS ====================

    /**
     * Gets all transactions for an account by account number
     */
    public List<Transaction> findByAccountNumber(String accountNumber) {
        BankAccount account = bankAccountPort.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new BusinessException("Cuenta no encontrada: " + accountNumber);
        }
        return transactionPort.findByAccountId(account.getId());
    }

    /**
     * Gets all transactions for an account by account ID
     */
    public List<Transaction> findByAccountId(Long accountId) {
        return transactionPort.findByAccountId(accountId);
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
    public Optional<Transaction> findById(Long id) {
        return transactionPort.findById(id);
    }

    /**
     * Gets all transactions in the system
     */
    public List<Transaction> findAll() {
        return transactionPort.findAll();
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
