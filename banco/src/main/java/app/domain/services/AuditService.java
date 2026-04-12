package app.domain.services;

import app.domain.models.*;
import app.domain.ports.OperationsLogPort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service for AUDIT LOGGING
 * Handles: Recording all system operations for audit trail and compliance
 */
public class AuditService {

    private final OperationsLogPort operationsLogPort;

    public AuditService(OperationsLogPort operationsLogPort) {
        this.operationsLogPort = operationsLogPort;
    }

    // ==================== GENERIC LOGGING ====================

    public void logOperation(String operationType, String entityId) {
        OperationsLog log = createBaseLog(operationType);

        Map<String, Object> details = new HashMap<>();
        details.put("entityId", entityId);
        details.put("timestamp", LocalDateTime.now().toString());

        log.setDetailData(details);
        operationsLogPort.save(log);
    }

    public void logOperationWithDetails(String operationType, Map<String, Object> details) {
        OperationsLog log = createBaseLog(operationType);
        log.setDetailData(details);
        operationsLogPort.save(log);
    }

    // ==================== TRANSACTION LOGGING ====================

    public void logTransaction(
            Transaction transaction,
            BigDecimal previousBalance,
            BigDecimal newBalance) {

        OperationsLog log = createBaseLog(transaction.getTransactionType().toString());

        Map<String, Object> details = new HashMap<>();
        details.put("transactionId", transaction.getId() != null ? transaction.getId().toString() : null);
        details.put("accountId", transaction.getAccount() != null && transaction.getAccount().getId() != null
                ? transaction.getAccount().getId().toString() : null);
        details.put("accountNumber", transaction.getAccount() != null
                ? transaction.getAccount().getAccountNumber() : null);
        details.put("amount", transaction.getAmount() != null ? transaction.getAmount().toString() : null);
        details.put("previousBalance", previousBalance != null ? previousBalance.toString() : null);
        details.put("newBalance", newBalance != null ? newBalance.toString() : null);
        if (previousBalance != null && newBalance != null) {
            details.put("balanceChange", newBalance.subtract(previousBalance).toString());
        }
        details.put("description", transaction.getDescription());
        details.put("transactionDate", transaction.getDate() != null ? transaction.getDate().toString() : null);

        log.setDetailData(details);
        operationsLogPort.save(log);
    }

    // ==================== ACCOUNT LOGGING ====================

    public void logAccountOpened(BankAccount account, BigDecimal initialDeposit) {
        OperationsLog log = createBaseLog("ACCOUNT_OPENED");

        Map<String, Object> details = new HashMap<>();
        details.put("accountId", account.getId() != null ? account.getId().toString() : null);
        details.put("accountNumber", account.getAccountNumber());
        details.put("accountType", account.getAccountType() != null ? account.getAccountType().toString() : null);
        details.put("clientId", account.getClient() != null && account.getClient().getId() != null
                ? account.getClient().getId().toString() : null);
        details.put("initialDeposit", initialDeposit != null ? initialDeposit.toString() : null);
        details.put("openingDate", account.getOpeningDate() != null ? account.getOpeningDate().toString() : null);

        log.setDetailData(details);
        operationsLogPort.save(log);
    }

    public void logAccountTypeChanged(
            String accountId,
            AccountType previousType,
            AccountType newType) {

        OperationsLog log = createBaseLog("ACCOUNT_TYPE_CHANGED");

        Map<String, Object> details = new HashMap<>();
        details.put("accountId", accountId);
        details.put("previousType", previousType != null ? previousType.toString() : null);
        details.put("newType", newType != null ? newType.toString() : null);

        log.setDetailData(details);
        operationsLogPort.save(log);
    }

    // ==================== TRANSFER LOGGING ====================

    public void logTransfer(Transfer transfer, String operation) {
        OperationsLog log = createBaseLog(operation);

        Map<String, Object> details = new HashMap<>();
        details.put("transferId", transfer.getId() != null ? transfer.getId().toString() : null);
        details.put("sourceAccountId", transfer.getSourceAccount() != null && transfer.getSourceAccount().getId() != null
                ? transfer.getSourceAccount().getId().toString() : null);
        details.put("targetAccountId", transfer.getTargetAccount() != null && transfer.getTargetAccount().getId() != null
                ? transfer.getTargetAccount().getId().toString() : null);
        details.put("amount", transfer.getAmount() != null ? transfer.getAmount().toString() : null);
        details.put("status", transfer.getTransferStatus() != null ? transfer.getTransferStatus().toString() : null);
        details.put("creationDate", transfer.getCreationDate() != null ? transfer.getCreationDate().toString() : null);

        if (transfer.getApprovalDate() != null) {
            details.put("approvalDate", transfer.getApprovalDate().toString());
        }

        log.setDetailData(details);
        operationsLogPort.save(log);
    }

    // ==================== LOAN LOGGING ====================

    public void logLoan(Loan loan, String operation) {
        OperationsLog log = createBaseLog(operation);

        Map<String, Object> details = new HashMap<>();
        details.put("loanId", loan.getId() != null ? loan.getId().toString() : null);
        details.put("clientId", loan.getClient() != null && loan.getClient().getId() != null
                ? loan.getClient().getId().toString() : null);
        details.put("requestedAmount", loan.getRequestedAmount() != null ? loan.getRequestedAmount().toString() : null);
        details.put("interestRate", loan.getInterestRate() != null ? loan.getInterestRate().toString() : null);
        details.put("termMonths", String.valueOf(loan.getTermMonths()));
        details.put("status", loan.getLoanStatus() != null ? loan.getLoanStatus().toString() : null);

        if (loan.getDisbursementTargetAccount() != null) {
            details.put("disbursementAccountId", loan.getDisbursementTargetAccount().getId() != null
                    ? loan.getDisbursementTargetAccount().getId().toString() : null);
        }

        log.setDetailData(details);
        operationsLogPort.save(log);
    }

    // ==================== USER LOGGING ====================

    public void logRoleChange(String userId, SystemRole previousRole, SystemRole newRole) {
        OperationsLog log = createBaseLog("USER_ROLE_CHANGED");

        Map<String, Object> details = new HashMap<>();
        details.put("userId", userId);
        details.put("previousRole", previousRole != null ? previousRole.toString() : null);
        details.put("newRole", newRole != null ? newRole.toString() : null);

        log.setDetailData(details);
        operationsLogPort.save(log);
    }

    public void logAuthenticationAttempt(String username, boolean success, String reason) {
        String operation = success ? "LOGIN_SUCCESS" : "LOGIN_FAILURE";
        OperationsLog log = createBaseLog(operation);

        Map<String, Object> details = new HashMap<>();
        details.put("username", username);
        details.put("success", String.valueOf(success));

        if (!success && reason != null) {
            details.put("failureReason", reason);
        }

        log.setDetailData(details);
        operationsLogPort.save(log);
    }

    // ==================== HELPER METHODS ====================

    private OperationsLog createBaseLog(String operationType) {
        OperationsLog log = new OperationsLog();
        log.setLogId(UUID.randomUUID().toString());
        log.setOperationType(operationType);
        log.setOperationDateTime(LocalDateTime.now());
        log.setDetailData(new HashMap<>());
        return log;
    }
}
