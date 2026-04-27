package app.application.usecases;

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

// TODO: Delegate business logic to domain services instead of implementing directly here

@Service
@RequiredArgsConstructor
public class TransferManagementUseCaseImpl implements TransferManagementUseCase {

    private final TransferPort transferPort;
    private final BankAccountPort bankAccountPort;
    private final OperationsLogPort operationsLogPort;

    private static final BigDecimal HIGH_VALUE_THRESHOLD = new BigDecimal("10000000");

    @Override
    @Transactional
    public Transfer requestTransfer(String sourceAccountNumber, String targetAccountNumber, BigDecimal amount) {
        validateAmount(amount);
        BankAccount source = bankAccountPort.findByAccountNumber(sourceAccountNumber);
        if (source == null) {
            throw new BusinessException("Source account not found");
        }
        BankAccount target = bankAccountPort.findByAccountNumber(targetAccountNumber);
        if (target == null) {
            throw new BusinessException("Target account not found");
        }

        if (source.getId() != null && source.getId().equals(target.getId())) {
            throw new BusinessException("Source and target must be different");
        }

        if (source.getCurrentBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds for transfer");
        }

        Transfer t = new Transfer(source, target, amount);
        t.setId(null);

        // HIGH VALUE LOGIC: if amount > threshold AND source account has CORPORATE client
        if (amount.compareTo(HIGH_VALUE_THRESHOLD) > 0
                && ((Client) source.getClient()) instanceof CorporateClient) {
            t.setTransferStatus(TransferStatus.AWAITING_APPROVAL);
        }

        Transfer saved = transferPort.save(t);
        registerLog("TRANSFER_REQUESTED", saved.getId() != null ? saved.getId().toString() : null);
        return saved;
    }

    @Override
    @Transactional
    public Transfer approveTransfer(String transferId, String auditorId) {
        Transfer t = transferPort.findById(Long.parseLong(transferId));
        if (t == null) {
            throw new BusinessException("Transfer not found");
        }

        if (t.isExpired()) {
            t.setTransferStatus(TransferStatus.EXPIRED);
            transferPort.save(t);
            throw new TransferExpiredException("Transfer has expired");
        }

        TransferStatus status = t.getTransferStatus();
        if (status != TransferStatus.PENDING && status != TransferStatus.AWAITING_APPROVAL) {
            throw new BusinessException("Transfer is not in PENDING or AWAITING_APPROVAL state");
        }

        BankAccount source = t.getSourceAccount();
        BankAccount target = t.getTargetAccount();

        source.debit(t.getAmount());
        target.credit(t.getAmount());

        bankAccountPort.save(source);
        bankAccountPort.save(target);

        t.setTransferStatus(TransferStatus.EXECUTED);
        t.setApprovalDate(LocalDateTime.now());

        Transfer updated = transferPort.save(t);
        registerLog("TRANSFER_APPROVED", updated.getId() != null ? updated.getId().toString() : null);
        return updated;
    }

    @Override
    @Transactional
    public Transfer rejectTransfer(String transferId, String reason) {
        Transfer t = transferPort.findById(Long.parseLong(transferId));
        if (t == null) {
            throw new BusinessException("Transfer not found");
        }

        TransferStatus status = t.getTransferStatus();
        if (status != TransferStatus.PENDING && status != TransferStatus.AWAITING_APPROVAL) {
            throw new BusinessException("Transfer is not in PENDING or AWAITING_APPROVAL state");
        }

        t.setTransferStatus(TransferStatus.REJECTED);
        Transfer updated = transferPort.save(t);
        registerLog("TRANSFER_REJECTED", updated.getId() != null ? updated.getId().toString() : null);
        return updated;
    }

    @Override
    public List<Transfer> findPendingTransfers() {
        return transferPort.findByTransferStatus(TransferStatus.PENDING);
    }

    @Override
    public Optional<Transfer> findById(String id) {
        return Optional.ofNullable(transferPort.findById(Long.parseLong(id)));
    }

    @Override
    public List<Transfer> findAll() {
        return transferPort.findAll();
    }

    @Scheduled(fixedRate = 60000)
    public void expirePendingTransfers() {
        List<Transfer> pending = transferPort.findByTransferStatus(TransferStatus.PENDING);
        for (Transfer t : pending) {
            if (t.isExpired()) {
                t.setTransferStatus(TransferStatus.EXPIRED);
                transferPort.save(t);
            }
        }
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than 0");
        }
    }

    private void registerLog(String operation, String transferId) {
        OperationsLog log = new OperationsLog();
        log.setLogId(UUID.randomUUID().toString());
        log.setOperationType(operation);
        log.setOperationDateTime(LocalDateTime.now());

        Map<String, Object> details = new HashMap<>();
        details.put("transferId", transferId);
        log.setDetailData(details);

        operationsLogPort.save(log);
    }
}
