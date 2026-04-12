package app.application.usecases;

import app.domain.Exceptions.BusinessException;
import app.domain.Exceptions.InvalidAmountException;
import app.domain.models.BankAccount;
import app.domain.models.OperationsLog;
import app.domain.models.Transfer;
import app.domain.models.TransferStatus;
import app.domain.models.User;
import app.domain.ports.BankAccountPort;
import app.domain.ports.OperationsLogPort;
import app.domain.ports.TransferPort;
import app.domain.ports.UserPort;
import app.domain.services.TransferDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferManagementUseCaseImpl implements TransferManagementUseCase {

    private final TransferPort transferPort;
    private final BankAccountPort bankAccountPort;
    private final OperationsLogPort operationsLogPort;
    private final TransferDomainService transferDomainService;
    private final UserPort userPort;

    @Override
    @Transactional
    public Transfer createTransfer(Transfer transfer) {
        validateAmount(transfer.getAmount());

        if (transfer.getSourceAccount() == null || transfer.getSourceAccount().getAccountNumber() == null) {
            throw new BusinessException("Cuenta de origen no encontrada");
        }
        if (transfer.getTargetAccount() == null || transfer.getTargetAccount().getAccountNumber() == null) {
            throw new BusinessException("Cuenta de destino no encontrada");
        }

        BankAccount source = bankAccountPort.findByAccountNumberForUpdate(
            transfer.getSourceAccount().getAccountNumber());
        if (source == null) {
            throw new BusinessException("Cuenta de origen no encontrada");
        }

        BankAccount target = bankAccountPort.findByAccountNumber(
            transfer.getTargetAccount().getAccountNumber());
        if (target == null) {
            throw new BusinessException("Cuenta de destino no encontrada");
        }

        transfer.setSourceAccount(source);
        transfer.setTargetAccount(target);

        // Validaciones de dominio (incluye alto monto y fecha)
        transferDomainService.validateTransferCreation(transfer);

        Transfer saved = transferPort.save(transfer);
        recordLog("TRANSFERENCIA_CREADA", saved, null);

        return saved;
    }

    @Override
    @Transactional
    public Transfer approveTransfer(Long transferId, Long userId) {
        Transfer transfer = transferPort.findById(transferId);
        if (transfer == null) {
            throw new BusinessException("Transferencia no encontrada");
        }

        transferDomainService.validateExpiry(transfer);

        if (transfer.getTransferStatus() != TransferStatus.PENDING
            && transfer.getTransferStatus() != TransferStatus.PENDING_APPROVAL) {
            throw new BusinessException("Solo se pueden aprobar transferencias PENDIENTES o PENDIENTES DE APROBACION");
        }

        BankAccount source = bankAccountPort.findByAccountNumberForUpdate(
            transfer.getSourceAccount().getAccountNumber());
        BankAccount target = bankAccountPort.findByAccountNumberForUpdate(
            transfer.getTargetAccount().getAccountNumber());

        if (source == null || target == null) {
            throw new BusinessException("Cuenta de origen o destino no encontrada");
        }

        source.debit(transfer.getAmount());
        target.credit(transfer.getAmount());

        bankAccountPort.save(source);
        bankAccountPort.save(target);

        transfer.setTransferStatus(TransferStatus.APPROVED);
        transfer.setApprovalDate(LocalDateTime.now());

        User user = userPort.findById(userId);
        transfer.setApproverUser(user);

        Transfer updated = transferPort.save(transfer);
        recordLog("TRANSFERENCIA_APROBADA", updated, user);

        return updated;
    }

    @Override
    public Transfer getTransferById(Long id) {
        Transfer transfer = transferPort.findById(id);
        if (transfer != null) {
            try {
                transferDomainService.validateExpiry(transfer);
            } catch (BusinessException e) {
                transferPort.save(transfer);
            }
        }
        return transfer;
    }

    @Override
    public List<Transfer> findAll() {
        return transferPort.findAll();
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
            throw new InvalidAmountException("El monto no puede tener mas de 2 decimales");
        }
    }

    // Registro de bitacora con referencias a entidades reales
    private void recordLog(String operation, Transfer t, User user) {
        OperationsLog log = new OperationsLog();
        log.setLogId(UUID.randomUUID().toString());
        log.setOperationDateTime(LocalDateTime.now());
        log.setOperationType(operation);
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
