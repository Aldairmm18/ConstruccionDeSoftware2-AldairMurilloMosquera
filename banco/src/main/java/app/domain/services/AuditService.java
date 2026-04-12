package app.domain.services;

import app.domain.models.*;
import app.domain.ports.OperationsLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service for AUDIT LOGGING
 * Handles: Recording all system operations for audit trail and compliance
 */
@Service
public class AuditService {
    
    @Autowired
    private OperationsLogRepository logRepository;
    
    // ==================== GENERIC LOGGING ====================
    
    public void logOperation(String operationType, String entityId) {
        OperationsLog log = createBaseLog(operationType);
        
        Map<String, String> details = new HashMap<>();
        details.put("entityId", entityId);
        details.put("timestamp", LocalDateTime.now().toString());
        
        log.setDetails(details);
        logRepository.save(log);
    }
    
    public void logOperationWithDetails(String operationType, Map<String, String> details) {
        OperationsLog log = createBaseLog(operationType);
        log.setDetails(details);
        logRepository.save(log);
    }
    
    // ==================== TRANSACTION LOGGING ====================
    
    public void logTransaction(
            Transaction transaction,
            BigDecimal previousBalance,
            BigDecimal newBalance) {
        
        OperationsLog log = createBaseLog(transaction.getTransactionType().toString());
        
        Map<String, String> details = new HashMap<>();
        details.put("transactionId", transaction.getId());
        details.put("accountId", transaction.getAccount().getId());
        details.put("accountNumber", transaction.getAccount().getAccountNumber());
        details.put("amount", transaction.getAmount().toString());
        details.put("previousBalance", previousBalance.toString());
        details.put("newBalance", newBalance.toString());
        details.put("balanceChange", newBalance.subtract(previousBalance).toString());
        details.put("description", transaction.getDescription());
        details.put("transactionDate", transaction.getDate().toString());
        
        log.setDetails(details);
        logRepository.save(log);
    }
    
    // ==================== ACCOUNT LOGGING ====================
    
    public void logAccountOpened(BankAccount account, BigDecimal initialDeposit) {
        OperationsLog log = createBaseLog("ACCOUNT_OPENED");
        
        Map<String, String> details = new HashMap<>();
        details.put("accountId", account.getId());
        details.put("accountNumber", account.getAccountNumber());
        details.put("accountType", account.getAccountType().toString());
        details.put("clientId", account.getClient().getId());
        details.put("initialDeposit", initialDeposit.toString());
        details.put("openingDate", account.getCreatedAt().toString());
        
        log.setDetails(details);
        logRepository.save(log);
    }
    
    public void logAccountTypeChanged(
            String accountId,
            AccountType previousType,
            AccountType newType) {
        
        OperationsLog log = createBaseLog("ACCOUNT_TYPE_CHANGED");
        
        Map<String, String> details = new HashMap<>();
        details.put("accountId", accountId);
        details.put("previousType", previousType.toString());
        details.put("newType", newType.toString());
        
        log.setDetails(details);
        logRepository.save(log);
    }
    
    // ==================== TRANSFER LOGGING ====================
    
    public void logTransfer(Transfer transfer, String operation) {
        OperationsLog log = createBaseLog(operation);
        
        Map<String, String> details = new HashMap<>();
        details.put("transferId", transfer.getId());
        details.put("originAccountId", transfer.getOriginAccount().getId());
        details.put("destinationAccountId", transfer.getDestinationAccount().getId());
        details.put("amount", transfer.getAmount().toString());
        details.put("status", transfer.getStatus().toString());
        details.put("creationDate", transfer.getCreatedAt().toString());
        
        if (transfer.getDate() != null) {
            details.put("executionDate", transfer.getDate().toString());
        }
        
        log.setDetails(details);
        logRepository.save(log);
    }
    
    // ==================== LOAN LOGGING ====================
    
    public void logLoan(Loan loan, String operation) {
        OperationsLog log = createBaseLog(operation);
        
        Map<String, String> details = new HashMap<>();
        details.put("loanId", loan.getId());
        details.put("clientId", loan.getClient().getId());
        details.put("amount", loan.getAmount().toString());
        details.put("interestRate", loan.getInterestRate().toString());
        details.put("termMonths", String.valueOf(loan.getTermMonths()));
        details.put("status", loan.getStatus().toString());
        details.put("requestDate", loan.getRequestDate().toString());
        
        if (loan.getDisbursementAccount() != null) {
            details.put("disbursementAccountId", loan.getDisbursementAccount().getId());
        }
        
        log.setDetails(details);
        logRepository.save(log);
    }
    
    // ==================== USER LOGGING ====================
    
    public void logRoleChange(String userId, SystemRole previousRole, SystemRole newRole) {
        OperationsLog log = createBaseLog("USER_ROLE_CHANGED");
        
        Map<String, String> details = new HashMap<>();
        details.put("userId", userId);
        details.put("previousRole", previousRole.toString());
        details.put("newRole", newRole.toString());
        
        log.setDetails(details);
        logRepository.save(log);
    }
    
    public void logAuthenticationAttempt(String username, boolean success, String reason) {
        String operation = success ? "LOGIN_SUCCESS" : "LOGIN_FAILURE";
        OperationsLog log = createBaseLog(operation);
        
        Map<String, String> details = new HashMap<>();
        details.put("username", username);
        details.put("success", String.valueOf(success));
        
        if (!success && reason != null) {
            details.put("failureReason", reason);
        }
        
        log.setDetails(details);
        logRepository.save(log);
    }
    
    // ==================== QUERY OPERATIONS ====================
    
    public List<OperationsLog> findAll() {
        return logRepository.findAll();
    }
    
    public List<OperationsLog> findByOperation(String operation) {
        return logRepository.findByOperation(operation);
    }
    
    public List<OperationsLog> findRecent(int limit) {
        List<OperationsLog> allLogs = logRepository.findAll();
        allLogs.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        return allLogs.subList(0, Math.min(limit, allLogs.size()));
    }
    
    // ==================== HELPER METHODS ====================
    
    private OperationsLog createBaseLog(String operationType) {
        OperationsLog log = new OperationsLog();
        log.setId(UUID.randomUUID().toString());
        log.setTimestamp(LocalDateTime.now());
        log.setOperation(operationType);
        return log;
    }
}
