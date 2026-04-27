package app.domain.services;

import app.domain.Exceptions.*;
import app.domain.models.*;
import app.domain.ports.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for TRANSFER OPERATIONS
 * Handles: Transfer requests, approvals, rejections, and auto-expiration
 */
public class TransferService {

    private final TransferPort transferPort;
    private final BankAccountPort bankAccountPort;
    private final ValidationService validationService;
    private final AuditService auditService;

    // HIGH VALUE TRANSFER: threshold for corporate accounts
    private static final BigDecimal HIGH_VALUE_THRESHOLD = new BigDecimal("10000000"); // 10 million COP

    public TransferService(TransferPort transferPort, BankAccountPort bankAccountPort,
                           ValidationService validationService, AuditService auditService) {
        this.transferPort = transferPort;
        this.bankAccountPort = bankAccountPort;
        this.validationService = validationService;
        this.auditService = auditService;
    }

    // ==================== CREATE OPERATIONS ====================

    /**
     * Requests a new transfer between accounts
     */
    public Transfer requestTransfer(
            String sourceAccountNumber,
            String targetAccountNumber,
            BigDecimal amount) {

        validationService.validateAmount(amount);

        BankAccount sourceAccount = findAccountOrThrow(sourceAccountNumber);
        BankAccount targetAccount = findAccountOrThrow(targetAccountNumber);

        validateDifferentAccounts(sourceAccount, targetAccount);
        validateSufficientBalance(sourceAccount, amount);

        Transfer transfer = new Transfer(sourceAccount, targetAccount, amount);
        transfer.setId(null);

        // HIGH VALUE LOGIC: if amount > threshold AND source account is CORPORATE client, set AWAITING_APPROVAL
        if (amount.compareTo(HIGH_VALUE_THRESHOLD) > 0
                && ((Client) sourceAccount.getClient()) instanceof CorporateClient) {
            transfer.setTransferStatus(TransferStatus.AWAITING_APPROVAL);
        }

        Transfer saved = transferPort.save(transfer);
        auditService.logOperation("TRANSFER_REQUESTED", saved.getId() != null ? saved.getId().toString() : null);

        return saved;
    }

    // ==================== APPROVAL OPERATIONS ====================

    /**
     * Approves a pending transfer and executes the money movement
     */
    public Transfer approveTransfer(Long transferId, String userId) {
        Transfer transfer = findTransferOrThrow(transferId);

        validateNotExpired(transfer);
        validatePendingOrAwaitingStatus(transfer);

        BankAccount sourceAccount = bankAccountPort.findById(transfer.getSourceAccount().getId());
        if (sourceAccount == null) {
            throw new BusinessException("Cuenta origen no encontrada");
        }
        BankAccount targetAccount = bankAccountPort.findById(transfer.getTargetAccount().getId());
        if (targetAccount == null) {
            throw new BusinessException("Cuenta destino no encontrada");
        }

        // Execute transfer
        sourceAccount.debit(transfer.getAmount());
        targetAccount.credit(transfer.getAmount());

        bankAccountPort.save(sourceAccount);
        bankAccountPort.save(targetAccount);

        // Update transfer status
        transfer.setTransferStatus(TransferStatus.APPROVED);
        transfer.setApprovalDate(LocalDateTime.now());

        Transfer updated = transferPort.save(transfer);
        auditService.logOperation("TRANSFER_APPROVED", updated.getId() != null ? updated.getId().toString() : null);

        return updated;
    }

    /**
     * Rejects a pending transfer
     */
    public Transfer rejectTransfer(Long transferId, String reason) {
        Transfer transfer = findTransferOrThrow(transferId);

        validatePendingOrAwaitingStatus(transfer);

        transfer.setTransferStatus(TransferStatus.REJECTED);
        Transfer updated = transferPort.save(transfer);

        auditService.logOperation("TRANSFER_REJECTED", updated.getId() != null ? updated.getId().toString() : null);

        return updated;
    }

    // ==================== QUERY OPERATIONS ====================

    public List<Transfer> findPendingTransfers() {
        return transferPort.findByTransferStatus(TransferStatus.PENDING);
    }

    public Transfer findById(Long id) {
        return transferPort.findById(id);
    }

    public List<Transfer> findAll() {
        return transferPort.findAll();
    }

    // ==================== SCHEDULED TASKS (called from use case layer) ====================

    /**
     * Auto-expires pending transfers older than 60 minutes
     * Called from use case layer which HAS @Scheduled
     */
    public void expirePendingTransfers() {
        List<Transfer> pendingTransfers = transferPort.findByTransferStatus(TransferStatus.PENDING);

        for (Transfer transfer : pendingTransfers) {
            if (transfer.isExpired()) {
                transfer.setTransferStatus(TransferStatus.EXPIRED);
                transferPort.save(transfer);
                auditService.logOperation("TRANSFER_EXPIRED",
                        transfer.getId() != null ? transfer.getId().toString() : null);
            }
        }
    }

    // ==================== VALIDATION METHODS ====================

    private BankAccount findAccountOrThrow(String accountNumber) {
        BankAccount account = bankAccountPort.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new BusinessException("Cuenta no encontrada: " + accountNumber);
        }
        return account;
    }

    private Transfer findTransferOrThrow(Long transferId) {
        Transfer transfer = transferPort.findById(transferId);
        if (transfer == null) {
            throw new BusinessException("Transferencia no encontrada: " + transferId);
        }
        return transfer;
    }

    private void validateDifferentAccounts(BankAccount source, BankAccount target) {
        if (source.getId() != null && source.getId().equals(target.getId())) {
            throw new IllegalArgumentException(
                "Cuenta origen y destino deben ser diferentes");
        }
    }

    private void validateSufficientBalance(BankAccount account, BigDecimal amount) {
        if (account.getCurrentBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(
                String.format("Saldo insuficiente. Disponible: %s, Requerido: %s",
                    account.getCurrentBalance(), amount));
        }
    }

    private void validateNotExpired(Transfer transfer) {
        if (transfer.isExpired()) {
            transfer.setTransferStatus(TransferStatus.EXPIRED);
            transferPort.save(transfer);
            throw new TransferExpiredException(
                "La transferencia expiró (límite: 60 minutos)");
        }
    }

    private void validatePendingOrAwaitingStatus(Transfer transfer) {
        TransferStatus status = transfer.getTransferStatus();
        if (status != TransferStatus.PENDING && status != TransferStatus.AWAITING_APPROVAL) {
            throw new IllegalStateException(
                "Solo se pueden procesar transferencias PENDIENTES o EN_ESPERA_APROBACION");
        }
    }
}
