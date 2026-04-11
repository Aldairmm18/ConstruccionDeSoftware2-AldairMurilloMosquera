package app.domain.services;

import app.domain.Exceptions.BusinessException;
import app.domain.Exceptions.InsufficientFundsException;
import app.domain.Exceptions.InvalidAmountException;
import app.domain.models.BankAccount;
import app.domain.models.Transfer;
import app.domain.models.TransferStatus;
import app.domain.models.OperationsLog;
import app.domain.ports.BankAccountPort;
import app.domain.ports.TransferPort;
import app.domain.ports.OperationsLogPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Consolidated Transaction Service.
 * Handles deposits, withdrawals, and transfers with full audit trail.
 */
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final BankAccountPort bankAccountPort;
    private final TransferPort transferPort;
    private final TransferDomainService transferDomainService;
    private final OperationsLogPort operationsLogPort;

    @Transactional
    public void deposit(String accountNumber, BigDecimal amount) {
        validateAmount(amount);

        BankAccount account = bankAccountPort.findByAccountNumberForUpdate(accountNumber);
        validateExistence(account);

        BigDecimal oldBalance = account.getCurrentBalance();
        account.credit(amount);
        bankAccountPort.save(account);

        // Record as a system-initiated deposit
        recordTransaction(null, account, amount, "DEPOSIT", oldBalance, account.getCurrentBalance());
    }

    @Transactional
    public void withdraw(String accountNumber, BigDecimal amount) {
        validateAmount(amount);

        BankAccount account = bankAccountPort.findByAccountNumberForUpdate(accountNumber);
        validateExistence(account);
        validateSufficientBalance(account, amount); // redundant now with debit() but okay

        BigDecimal oldBalance = account.getCurrentBalance();
        account.debit(amount);
        bankAccountPort.save(account);

        // Record as a system-initiated withdrawal
        recordTransaction(account, null, amount, "WITHDRAWAL", oldBalance, account.getCurrentBalance());
    }

    @Transactional
    public void transfer(String sourceNumber, String targetNumber, BigDecimal amount) {
        validateAmount(amount);
        
        BankAccount source = bankAccountPort.findByAccountNumberForUpdate(sourceNumber);
        BankAccount target = bankAccountPort.findByAccountNumberForUpdate(targetNumber);

        validateExistence(source);
        validateExistence(target);
        
        // Create model for validation
        Transfer transferModel = new Transfer();
        transferModel.setSourceAccount(source);
        transferModel.setTargetAccount(target);
        transferModel.setAmount(amount);
        
        // Use domain service for validations
        transferDomainService.validateTransferCreation(transferModel);

        BigDecimal oldSourceBalance = source.getCurrentBalance();
        BigDecimal oldTargetBalance = target.getCurrentBalance();

        // Execute balances update
        source.debit(amount);
        target.credit(amount);

        bankAccountPort.save(source);
        bankAccountPort.save(target);

        // Save entry in Ledger (Transfer table)
        transferModel.setTransferStatus(TransferStatus.EXECUTED);
        transferModel.setCreationDate(LocalDateTime.now());
        transferModel.setApprovalDate(LocalDateTime.now());
        transferPort.save(transferModel);
        
        recordTransaction(source, target, amount, "TRANSFER", oldSourceBalance, source.getCurrentBalance());
    }

    private void recordTransaction(BankAccount source, BankAccount target, BigDecimal amount, String type, BigDecimal saldoAnterior, BigDecimal saldoNuevo) {
        // Also keep writing to transfer port for ledger
        if (!"TRANSFER".equals(type)) {
            Transfer record = new Transfer();
            record.setSourceAccount(source);
            record.setTargetAccount(target);
            record.setAmount(amount);
            record.setTransferStatus(TransferStatus.EXECUTED);
            record.setCreationDate(LocalDateTime.now());
            record.setApprovalDate(LocalDateTime.now());
            transferPort.save(record);
        }
        
        OperationsLog log = new OperationsLog();
        log.setLogId(UUID.randomUUID().toString());
        log.setOperationDateTime(LocalDateTime.now());
        log.setOperationType(type);
        
        Map<String, Object> detalles = new HashMap<>();
        if (source != null) detalles.put("cuentaOrigen", source.getAccountNumber());
        if (target != null) detalles.put("cuentaDestino", target.getAccountNumber());
        detalles.put("monto", amount);
        if (saldoAnterior != null) detalles.put("saldoAnterior", saldoAnterior);
        if (saldoNuevo != null) detalles.put("saldoNuevo", saldoNuevo);
        log.setDetailData(detalles);
        
        operationsLogPort.save(log);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new InvalidAmountException("Monto no puede ser nulo");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Transaction amount must be greater than zero");
        }
        if (amount.compareTo(new BigDecimal("999999999.99")) > 0) {
            throw new InvalidAmountException("Monto excede límite permitido");
        }
        if (amount.scale() > 2) {
            throw new InvalidAmountException("Monto no puede tener más de 2 decimales");
        }
    }

    private void validateExistence(BankAccount account) {
        if (account == null) {
            throw new BusinessException("Account does not exist");
        }
    }

    private void validateSufficientBalance(BankAccount account, BigDecimal amount) {
        if (account.getCurrentBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient balance for operation");
        }
    }

    @Transactional
    public Transfer approve(Long transferId, Long userId) {
        Transfer t = transferPort.findById(transferId);
        if (t == null) {
            throw new RuntimeException("Transferencia no encontrada");
        }
        
        if (t.isExpired()) {
            t.setTransferStatus(TransferStatus.EXPIRED);
            transferPort.save(t);
            throw new app.domain.Exceptions.TransferenciaExpiradaException("La transferencia expiró");
        }
        
        if (t.getTransferStatus() != TransferStatus.PENDING) {
            throw new IllegalStateException("Solo se pueden aprobar transferencias PENDIENTES");
        }
        
        BankAccount source = t.getSourceAccount();
        BankAccount target = t.getTargetAccount();
        
        source.debit(t.getAmount());
        target.credit(t.getAmount());
        
        bankAccountPort.save(source);
        bankAccountPort.save(target);
        
        t.setTransferStatus(TransferStatus.APPROVED);
        t.setApprovalDate(LocalDateTime.now());
        t.setApproverUserId(userId);
        
        Transfer updated = transferPort.save(t);
        recordTransaction(source, target, t.getAmount(), "TRANSFER_APROBADA", source.getCurrentBalance(), target.getCurrentBalance());
        return updated;
    }

    @org.springframework.scheduling.annotation.Scheduled(fixedRate = 60000)
    @Transactional
    public void expirePendingTransfers() {
        java.util.List<Transfer> pending = transferPort.findByTransferStatus(TransferStatus.PENDING);
        for (Transfer t : pending) {
            if (t.isExpired()) {
                t.setTransferStatus(TransferStatus.EXPIRED);
                transferPort.save(t);
                recordTransaction(t.getSourceAccount(), t.getTargetAccount(), t.getAmount(), "TRANSFERENCIA_EXPIRADA", null, null);
            }
        }
    }
}
