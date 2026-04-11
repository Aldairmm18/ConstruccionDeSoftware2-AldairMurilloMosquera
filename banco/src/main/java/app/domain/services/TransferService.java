package app.domain.services;

import app.domain.Exceptions.*;
import app.domain.models.*;
import app.domain.ports.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TransferService {
    
    private final TransferPort transferPort;
    private final BankAccountPort bankAccountPort;
    private final OperationsLogPort operationsLogPort;
    
    @Transactional
    public Transfer createTransfer(Transfer transfer) {
        validateAmount(transfer.getAmount());
        
        if (transfer.getSourceAccount().getAccountNumber()
                .equals(transfer.getTargetAccount().getAccountNumber())) {
            throw new IllegalArgumentException("Source and target accounts must be different");
        }
        
        BankAccount source = bankAccountPort.findByAccountNumberForUpdate(transfer.getSourceAccount().getAccountNumber());
        if (source == null) throw new BusinessException("Source account not found");
        
        if (source.getCurrentBalance().compareTo(transfer.getAmount()) < 0) {
            throw new InsufficientFundsException(
                String.format("Insufficient balance. Available: %s, Required: %s",
                    source.getCurrentBalance(), transfer.getAmount()));
        }
        
        BankAccount target = bankAccountPort.findByAccountNumber(transfer.getTargetAccount().getAccountNumber());
        if (target == null) throw new BusinessException("Target account not found");
        
        transfer.setTransferStatus(TransferStatus.PENDING);
        transfer.setCreationDate(LocalDateTime.now());
        
        Transfer saved = transferPort.save(transfer);
        recordLog("TRANSFER_CREATED", saved, null);
        
        return saved;
    }
    
    @Transactional
    public Transfer approveTransfer(Long transferId, Long userId) {
        Transfer t = transferPort.findById(transferId);
        if (t == null) throw new BusinessException("Transfer not found");
        
        if (t.isExpired()) {
            t.setTransferStatus(TransferStatus.EXPIRED);
            transferPort.save(t);
            throw new TransferExpiredException("Transfer expired (60 minute limit)");
        }
        
        if (t.getTransferStatus() != TransferStatus.PENDING) {
            throw new IllegalStateException("Only PENDING transfers can be approved");
        }
        
        BankAccount source = bankAccountPort.findByAccountNumberForUpdate(t.getSourceAccount().getAccountNumber());
        BankAccount target = bankAccountPort.findByAccountNumberForUpdate(t.getTargetAccount().getAccountNumber());
        
        source.debit(t.getAmount());
        target.credit(t.getAmount());
        
        bankAccountPort.save(source);
        bankAccountPort.save(target);
        
        t.setTransferStatus(TransferStatus.APPROVED);
        t.setApprovalDate(LocalDateTime.now());
        t.setApproverUserId(userId);
        
        Transfer updated = transferPort.save(t);
        recordLog("TRANSFER_APPROVED", updated, String.valueOf(userId));
        
        return updated;
    }
    
    public List<Transfer> findAll() {
        return transferPort.findAll();
    }
    
    public Transfer findById(Long id) {
        return transferPort.findById(id);
    }
    
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void expirePendingTransfers() {
        List<Transfer> pending = transferPort.findByTransferStatus(TransferStatus.PENDING);
        for (Transfer t : pending) {
            if (t.isExpired()) {
                t.setTransferStatus(TransferStatus.EXPIRED);
                transferPort.save(t);
                recordLog("TRANSFER_EXPIRED_AUTOMATICALLY", t, "SYSTEM");
            }
        }
    }
    
    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than 0");
        }
        if (amount.scale() > 2) {
            throw new InvalidAmountException("Amount cannot have more than 2 decimal places");
        }
    }
    
    private void recordLog(String operation, Transfer t, String userId) {
        OperationsLog log = new OperationsLog();
        log.setLogId(UUID.randomUUID().toString());
        log.setOperationDateTime(LocalDateTime.now());
        log.setOperationType(operation);
        
        Map<String, Object> details = new HashMap<>();
        details.put("transferId", t.getId());
        details.put("sourceAccount", t.getSourceAccount().getAccountNumber());
        details.put("targetAccount", t.getTargetAccount().getAccountNumber());
        details.put("amount", t.getAmount());
        details.put("status", t.getTransferStatus().toString());
        if (userId != null) details.put("userId", userId);
        log.setDetailData(details);
        
        operationsLogPort.save(log);
    }
}
