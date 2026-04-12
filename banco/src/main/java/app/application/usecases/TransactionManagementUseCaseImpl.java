package app.application.usecases;

import app.domain.Exceptions.BusinessException;
import app.domain.Exceptions.InvalidAmountException;
import app.domain.models.BankAccount;
import app.domain.models.OperationsLog;
import app.domain.models.Transfer;
import app.domain.models.TransferStatus;
import app.domain.ports.BankAccountPort;
import app.domain.ports.OperationsLogPort;
import app.domain.ports.TransferPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionManagementUseCaseImpl implements TransactionManagementUseCase {

    private final BankAccountPort bankAccountPort;
    private final OperationsLogPort operationsLogPort;
    private final TransferPort transferPort;

    @Override
    @Transactional
    public void deposit(String accountNumber, BigDecimal amount) {
        validateAmount(amount);

        BankAccount account = bankAccountPort.findByAccountNumberForUpdate(accountNumber);
        if (account == null) {
            throw new BusinessException("Cuenta no encontrada");
        }

        BigDecimal oldBalance = account.getCurrentBalance();
        account.credit(amount);
        bankAccountPort.save(account);

        recordLog("DEPOSITO", account, amount, oldBalance);
        recordLedger(null, account, amount);
    }

    @Override
    @Transactional
    public void withdraw(String accountNumber, BigDecimal amount) {
        validateAmount(amount);

        BankAccount account = bankAccountPort.findByAccountNumberForUpdate(accountNumber);
        if (account == null) {
            throw new BusinessException("Cuenta no encontrada");
        }

        BigDecimal oldBalance = account.getCurrentBalance();
        account.debit(amount);
        bankAccountPort.save(account);

        recordLog("RETIRO", account, amount, oldBalance);
        recordLedger(account, null, amount);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new InvalidAmountException("El monto no puede ser nulo");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("El monto debe ser mayor a 0");
        }
        if (amount.compareTo(new BigDecimal("999999999.99")) > 0) {
            throw new InvalidAmountException("El monto excede el limite permitido");
        }
        if (amount.scale() > 2) {
            throw new InvalidAmountException("El monto no puede tener mas de 2 decimales");
        }
    }

    // Registro de bitacora con referencias a entidades reales
    private void recordLog(String type, BankAccount account, BigDecimal amount, BigDecimal oldBalance) {
        OperationsLog log = new OperationsLog();
        log.setLogId(UUID.randomUUID().toString());
        log.setOperationDateTime(LocalDateTime.now());
        log.setOperationType(type);
        log.setAffectedProduct(account);
        log.setUser(null);

        Map<String, Object> details = new HashMap<>();
        details.put("accountNumber", account.getAccountNumber());
        details.put("amount", amount);
        details.put("oldBalance", oldBalance);
        details.put("newBalance", account.getCurrentBalance());
        log.setDetailData(details);

        operationsLogPort.save(log);
    }

    private void recordLedger(BankAccount source, BankAccount target, BigDecimal amount) {
        Transfer record = new Transfer();
        record.setSourceAccount(source);
        record.setTargetAccount(target);
        record.setAmount(amount);
        record.setTransferStatus(TransferStatus.COMPLETED);
        record.setCreationDate(LocalDateTime.now());
        record.setApprovalDate(LocalDateTime.now());
        transferPort.save(record);
    }
}
