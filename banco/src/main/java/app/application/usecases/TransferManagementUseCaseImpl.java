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

@Service
@RequiredArgsConstructor
public class TransferManagementUseCaseImpl implements TransferManagementUseCase {

    private final TransferRepository transferRepository;
    private final BankAccountRepository bankAccountRepository;
    private final OperationsLogRepository operationsLogRepository;

    @Override
    @Transactional
    public Transfer requestTransfer(String sourceAccountNumber, String targetAccountNumber, BigDecimal amount) {
        validateAmount(amount);
        BankAccount source = bankAccountRepository.findByAccountNumber(sourceAccountNumber)
                .orElseThrow(() -> new BusinessException("Source account not found"));
        BankAccount target = bankAccountRepository.findByAccountNumber(targetAccountNumber)
                .orElseThrow(() -> new BusinessException("Target account not found"));

        if (source.getId().equals(target.getId())) {
            throw new BusinessException("Source and target must be different");
        }

        if (source.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds for transfer");
        }

        Transfer t = new Transfer(source, target, amount);
        t.setId(UUID.randomUUID().toString());
        Transfer saved = transferRepository.save(t);
        registerLog("TRANSFER_REQUESTED", saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public Transfer approveTransfer(String transferId, String auditorId) {
        Transfer t = transferRepository.findById(transferId)
                .orElseThrow(() -> new BusinessException("Transfer not found"));

        if (t.isExpired()) {
            t.setStatus(TransferStatus.EXPIRED);
            transferRepository.save(t);
            throw new TransferExpiredException("Transfer has expired");
        }

        if (t.getStatus() != TransferStatus.PENDING) {
            throw new BusinessException("Transfer is not in PENDING state");
        }

        BankAccount source = t.getOriginAccount();
        BankAccount target = t.getDestinationAccount();

        source.debit(t.getAmount());
        target.credit(t.getAmount());

        bankAccountRepository.save(source);
        bankAccountRepository.save(target);

        t.setStatus(TransferStatus.APPROVED);
        t.setDate(LocalDateTime.now());
        
        Transfer updated = transferRepository.save(t);
        registerLog("TRANSFER_APPROVED", updated.getId());
        return updated;
    }

    @Override
    @Transactional
    public Transfer rejectTransfer(String transferId, String reason) {
        Transfer t = transferRepository.findById(transferId)
                .orElseThrow(() -> new BusinessException("Transfer not found"));

        if (t.getStatus() != TransferStatus.PENDING) {
            throw new BusinessException("Transfer is not in PENDING state");
        }

        t.setStatus(TransferStatus.REJECTED);
        Transfer updated = transferRepository.save(t);
        registerLog("TRANSFER_REJECTED", updated.getId());
        return updated;
    }

    @Override
    public List<Transfer> findPendingTransfers() {
        return transferRepository.findByStatus(TransferStatus.PENDING);
    }

    @Override
    public Optional<Transfer> findById(String id) {
        return transferRepository.findById(id);
    }

    @Override
    public List<Transfer> findAll() {
        return transferRepository.findAll();
    }

    @Scheduled(fixedRate = 60000)
    public void expirePendingTransfers() {
        List<Transfer> pending = transferRepository.findByStatus(TransferStatus.PENDING);
        for (Transfer t : pending) {
            if (t.isExpired()) {
                t.setStatus(TransferStatus.EXPIRED);
                transferRepository.save(t);
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
        log.setId(UUID.randomUUID().toString());
        log.setTimestamp(LocalDateTime.now());
        log.setOperation(operation);
        
        Map<String, String> details = new HashMap<>();
        details.put("transferId", transferId);
        log.setDetails(details);
        
        operationsLogRepository.save(log);
    }
}
