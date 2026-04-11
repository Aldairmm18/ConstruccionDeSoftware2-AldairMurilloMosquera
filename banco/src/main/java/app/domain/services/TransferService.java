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
            throw new IllegalArgumentException("Las cuentas de origen y destino deben ser diferentes");
        }
        
        BankAccount source = bankAccountPort.findByAccountNumberForUpdate(transfer.getSourceAccount().getAccountNumber());
        if (source == null) throw new BusinessException("Cuenta de origen no encontrada");
        
        if (source.getCurrentBalance().compareTo(transfer.getAmount()) < 0) {
            throw new InsufficientFundsException(
                String.format("Saldo insuficiente. Disponible: %s, Requerido: %s",
                    source.getCurrentBalance(), transfer.getAmount()));
        }
        
        BankAccount target = bankAccountPort.findByAccountNumber(transfer.getTargetAccount().getAccountNumber());
        if (target == null) throw new BusinessException("Cuenta de destino no encontrada");
        
        transfer.setTransferStatus(TransferStatus.PENDING);
        transfer.setCreationDate(LocalDateTime.now());
        
        Transfer saved = transferPort.save(transfer);
        recordLog("TRANSFERENCIA_CREADA", saved, null);
        
        return saved;
    }
    
    @Transactional
    public Transfer approveTransfer(Long transferId, Long userId) {
        Transfer t = transferPort.findById(transferId);
        if (t == null) throw new BusinessException("Transferencia no encontrada");
        
        if (t.isExpired()) {
            t.setTransferStatus(TransferStatus.EXPIRED);
            transferPort.save(t);
            throw new TransferExpiredException("La transferencia ha expirado (límite de 60 minutos)");
        }
        
        if (t.getTransferStatus() != TransferStatus.PENDING) {
            throw new IllegalStateException("Solo se pueden aprobar transferencias PENDIENTES");
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
        recordLog("TRANSFERENCIA_APROBADA", updated, String.valueOf(userId));
        
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
                recordLog("TRANSFERENCIA_EXPIRADA_AUTOMATICAMENTE", t, "SYSTEM");
            }
        }
    }
    
    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("El monto debe ser mayor a 0");
        }
        if (amount.scale() > 2) {
            throw new InvalidAmountException("El monto no puede tener más de 2 decimales");
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
