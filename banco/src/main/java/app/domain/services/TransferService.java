package app.domain.services;

import app.domain.Exceptions.*;
import app.domain.models.*;
import app.domain.ports.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for TRANSFER OPERATIONS
 * Handles: Transfer requests, approvals, rejections, and auto-expiration
 */
@Service
public class TransferService {
    
    @Autowired
    private TransferRepository transferRepository;
    
    @Autowired
    private BankAccountRepository accountRepository;
    
    @Autowired
    private ValidationService validationService;
    
    @Autowired
    private AuditService auditService;
    
    // ==================== CREATE OPERATIONS ====================
    
    /**
     * Requests a new transfer between accounts
     */
    @Transactional
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
        transfer.setId(UUID.randomUUID().toString());
        
        Transfer saved = transferRepository.save(transfer);
        auditService.logOperation("TRANSFER_REQUESTED", saved.getId());
        
        return saved;
    }
    
    // ==================== APPROVAL OPERATIONS ====================
    
    /**
     * Approves a pending transfer and executes the money movement
     */
    @Transactional
    public Transfer approveTransfer(String transferId, String userId) {
        Transfer transfer = findTransferOrThrow(transferId);
        
        validateNotExpired(transfer);
        validatePendingStatus(transfer);
        
        BankAccount sourceAccount = accountRepository.findById(transfer.getOriginAccount().getId())
            .orElseThrow(() -> new BusinessException("Cuenta origen no encontrada"));
        BankAccount targetAccount = accountRepository.findById(transfer.getDestinationAccount().getId())
            .orElseThrow(() -> new BusinessException("Cuenta destino no encontrada"));
        
        // Execute transfer
        sourceAccount.debit(transfer.getAmount());
        targetAccount.credit(transfer.getAmount());
        
        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);
        
        // Update transfer status
        transfer.setStatus(TransferStatus.APPROVED);
        transfer.setDate(LocalDateTime.now());
        
        Transfer updated = transferRepository.save(transfer);
        auditService.logOperation("TRANSFER_APPROVED", updated.getId());
        
        return updated;
    }
    
    /**
     * Rejects a pending transfer
     */
    @Transactional
    public Transfer rejectTransfer(String transferId, String reason) {
        Transfer transfer = findTransferOrThrow(transferId);
        
        validatePendingStatus(transfer);
        
        transfer.setStatus(TransferStatus.REJECTED);
        Transfer updated = transferRepository.save(transfer);
        
        auditService.logOperation("TRANSFER_REJECTED", updated.getId());
        
        return updated;
    }
    
    // ==================== QUERY OPERATIONS ====================
    
    public List<Transfer> findPendingTransfers() {
        return transferRepository.findByStatus(TransferStatus.PENDING);
    }
    
    public Optional<Transfer> findById(String id) {
        return transferRepository.findById(id);
    }
    
    public List<Transfer> findAll() {
        return transferRepository.findAll();
    }
    
    // ==================== SCHEDULED TASKS ====================
    
    /**
     * Auto-expires pending transfers older than 60 minutes
     * Runs every minute
     */
    @Scheduled(fixedRate = 60000)
    public void expirePendingTransfers() {
        List<Transfer> pendingTransfers = transferRepository
            .findByStatus(TransferStatus.PENDING);
        
        for (Transfer transfer : pendingTransfers) {
            if (transfer.isExpired()) {
                transfer.setStatus(TransferStatus.EXPIRED);
                transferRepository.save(transfer);
                auditService.logOperation("TRANSFER_EXPIRED", transfer.getId());
            }
        }
    }
    
    // ==================== VALIDATION METHODS ====================
    
    private BankAccount findAccountOrThrow(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new BusinessException(
                "Cuenta no encontrada: " + accountNumber));
    }
    
    private Transfer findTransferOrThrow(String transferId) {
        return transferRepository.findById(transferId)
            .orElseThrow(() -> new BusinessException(
                "Transferencia no encontrada: " + transferId));
    }
    
    private void validateDifferentAccounts(BankAccount source, BankAccount target) {
        if (source.getId().equals(target.getId())) {
            throw new IllegalArgumentException(
                "Cuenta origen y destino deben ser diferentes");
        }
    }
    
    private void validateSufficientBalance(BankAccount account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(
                String.format("Saldo insuficiente. Disponible: %s, Requerido: %s",
                    account.getBalance(), amount));
        }
    }
    
    private void validateNotExpired(Transfer transfer) {
        if (transfer.isExpired()) {
            transfer.setStatus(TransferStatus.EXPIRED);
            transferRepository.save(transfer);
            throw new TransferExpiredException(
                "La transferencia expiró (límite: 60 minutos)");
        }
    }
    
    private void validatePendingStatus(Transfer transfer) {
        if (transfer.getStatus() != TransferStatus.PENDING) {
            throw new IllegalStateException(
                "Solo se pueden procesar transferencias PENDIENTES");
        }
    }
}
