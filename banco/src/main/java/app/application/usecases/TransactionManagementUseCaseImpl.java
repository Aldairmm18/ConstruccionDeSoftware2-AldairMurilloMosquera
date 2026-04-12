package app.application.usecases;

import app.domain.Exceptions.BusinessException;
import app.domain.Exceptions.InvalidAmountException;
import app.domain.models.*;
import app.domain.ports.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

// TODO: Delegate business logic to domain services instead of implementing directly here

@Service
@RequiredArgsConstructor
public class TransactionManagementUseCaseImpl implements TransactionManagementUseCase {

    private final TransactionPort transactionPort;
    private final BankAccountPort bankAccountPort;
    private final OperationsLogPort operationsLogPort;

    @Override
    @Transactional
    public Transaction makeDeposit(String accountNumber, BigDecimal amount, String description) {
        validateAmount(amount);
        BankAccount account = bankAccountPort.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new BusinessException("Account not found");
        }

        account.credit(amount);
        bankAccountPort.save(account);

        Transaction t = new Transaction();
        t.setId(null);
        t.setAccount(account);
        t.setAmount(amount);
        t.setTransactionType(TransactionType.DEPOSIT);
        t.setDate(LocalDateTime.now());
        t.setDescription(description != null ? description : "Deposit");

        Transaction saved = transactionPort.save(t);
        registerLog("DEPOSIT", saved.getId() != null ? saved.getId().toString() : null);
        return saved;
    }

    @Override
    @Transactional
    public Transaction makeWithdrawal(String accountNumber, BigDecimal amount, String description) {
        validateAmount(amount);
        BankAccount account = bankAccountPort.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new BusinessException("Account not found");
        }

        account.debit(amount);
        bankAccountPort.save(account);

        Transaction t = new Transaction();
        t.setId(null);
        t.setAccount(account);
        t.setAmount(amount);
        t.setTransactionType(TransactionType.WITHDRAWAL);
        t.setDate(LocalDateTime.now());
        t.setDescription(description != null ? description : "Withdrawal");

        Transaction saved = transactionPort.save(t);
        registerLog("WITHDRAWAL", saved.getId() != null ? saved.getId().toString() : null);
        return saved;
    }

    @Override
    @Transactional
    public Transaction payService(String accountNumber, String serviceName, String reference, BigDecimal amount) {
        validateAmount(amount);
        BankAccount account = bankAccountPort.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new BusinessException("Account not found");
        }

        account.debit(amount);
        bankAccountPort.save(account);

        Transaction t = new Transaction();
        t.setId(null);
        t.setAccount(account);
        t.setAmount(amount);
        t.setTransactionType(TransactionType.SERVICE_PAYMENT);
        t.setDate(LocalDateTime.now());
        t.setDescription(String.format("Payment %s - Ref: %s", serviceName, reference));

        Transaction saved = transactionPort.save(t);
        registerLog("SERVICE_PAYMENT", saved.getId() != null ? saved.getId().toString() : null);
        return saved;
    }

    @Override
    public List<Transaction> getTransactionsByAccount(String accountNumber) {
        BankAccount account = bankAccountPort.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new BusinessException("Account not found");
        }
        return transactionPort.findByAccountId(account.getId());
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than 0");
        }
    }

    private void registerLog(String operation, String transactionId) {
        OperationsLog log = new OperationsLog();
        log.setLogId(UUID.randomUUID().toString());
        log.setOperationType(operation);
        log.setOperationDateTime(LocalDateTime.now());

        Map<String, Object> details = new HashMap<>();
        details.put("transactionId", transactionId);
        log.setDetailData(details);

        operationsLogPort.save(log);
    }
}
