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
    private final TransferDomainService transferDomainService;
    private final UserPort userPort;
    
    @Transactional
    public Transfer createTransfer(Transfer transfer) {
        validateAmount(transfer.getAmount());
        
        BankAccount source = bankAccountPort.findByAccountNumberForUpdate(transfer.getSourceAccount().getAccountNumber());
        if (source == null) throw new BusinessException("Cuenta de origen no encontrada");
        transfer.setSourceAccount(source);
        
        BankAccount target = bankAccountPort.findByAccountNumber(transfer.getTargetAccount().getAccountNumber());
        if (target == null) throw new BusinessException("Cuenta de destino no encontrada");
        transfer.setTargetAccount(target);

        // CORRECCIÓN 1: Uso del servicio de dominio para validaciones y alto monto
        transferDomainService.validateTransferCreation(transfer);
        
        Transfer saved = transferPort.save(transfer);
        recordLog("TRANSFERENCIA_CREADA", saved, null);
        
        return saved;
    }
    
    @Transactional
    public Transfer approveTransfer(Long transferId, Long userId) {
        Transfer t = transferPort.findById(transferId);
        if (t == null) throw new BusinessException("Transferencia no encontrada");
        
        // CORRECCIÓN 1: Validación de vencimiento
        transferDomainService.validateExpiry(t);
        
        if (t.getTransferStatus() != TransferStatus.PENDING && t.getTransferStatus() != TransferStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Solo se pueden aprobar transferencias PENDIENTES o PENDIENTES DE APROBACIÓN");
        }
        
        BankAccount source = bankAccountPort.findByAccountNumberForUpdate(t.getSourceAccount().getAccountNumber());
        BankAccount target = bankAccountPort.findByAccountNumberForUpdate(t.getTargetAccount().getAccountNumber());
        
        source.debit(t.getAmount());
        target.credit(t.getAmount());
        
        bankAccountPort.save(source);
        bankAccountPort.save(target);
        
        t.setTransferStatus(TransferStatus.APPROVED);
        t.setApprovalDate(LocalDateTime.now());
        
        User user = userPort.findById(userId);
        t.setApproverUser(user);
        
        Transfer updated = transferPort.save(t);
        recordLog("TRANSFERENCIA_APROBADA", updated, user);
        
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
        pending.addAll(transferPort.findByTransferStatus(TransferStatus.PENDING_APPROVAL));
        
        for (Transfer t : pending) {
            try {
                transferDomainService.validateExpiry(t);
            } catch (BusinessException e) {
                t.setTransferStatus(TransferStatus.EXPIRED);
                transferPort.save(t);
                recordLog("TRANSFERENCIA_EXPIRADA_AUTOMATICAMENTE", t, null);
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
    
    // CORRECCIÓN 2: OperationsLog con referencias a entidades de dominio
    private void recordLog(String operation, Transfer t, User user) {
        OperationsLog log = new OperationsLog();
        log.setLogId(UUID.randomUUID().toString());
        log.setOperationDateTime(LocalDateTime.now());
        log.setOperationType(operation);
        
        // Referencias a objetos de dominio
        log.setAffectedProduct(t.getSourceAccount());
        log.setUser(user);
        
        Map<String, Object> details = new HashMap<>();
        details.put("transferId", t.getId());
        details.put("sourceAccount", t.getSourceAccount().getAccountNumber());
        details.put("targetAccount", t.getTargetAccount().getAccountNumber());
        details.put("amount", t.getAmount());
        details.put("status", t.getTransferStatus().toString());
        log.setDetailData(details);
        
        operationsLogPort.save(log);
    }
}
