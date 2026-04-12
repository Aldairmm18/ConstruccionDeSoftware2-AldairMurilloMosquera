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

@Service
@RequiredArgsConstructor
public class TransactionManagementUseCaseImpl implements TransactionManagementUseCase {

    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final OperationsLogRepository operationsLogRepository;

    @Override
    @Transactional
    public Transaction makeDeposit(String accountNumber, BigDecimal amount, String description) {
        validateAmount(amount);
        BankAccount account = bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BusinessException("Account not found"));

        account.credit(amount);
        bankAccountRepository.save(account);

        Transaction t = new Transaction();
        t.setId(UUID.randomUUID().toString());
        t.setAccount(account);
        t.setAmount(amount);
        t.setTransactionType(TransactionType.DEPOSIT);
        t.setDate(LocalDateTime.now());
        t.setDescription(description != null ? description : "Deposit");

        Transaction saved = transactionRepository.save(t);
        registerLog("DEPOSIT", saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public Transaction makeWithdrawal(String accountNumber, BigDecimal amount, String description) {
        validateAmount(amount);
        BankAccount account = bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BusinessException("Account not found"));

        account.debit(amount);
        bankAccountRepository.save(account);

        Transaction t = new Transaction();
        t.setId(UUID.randomUUID().toString());
        t.setAccount(account);
        t.setAmount(amount);
        t.setTransactionType(TransactionType.WITHDRAWAL);
        t.setDate(LocalDateTime.now());
        t.setDescription(description != null ? description : "Withdrawal");

        Transaction saved = transactionRepository.save(t);
        registerLog("WITHDRAWAL", saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public Transaction payService(String accountNumber, String serviceName, String reference, BigDecimal amount) {
        validateAmount(amount);
        BankAccount account = bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BusinessException("Account not found"));

        account.debit(amount);
        bankAccountRepository.save(account);

        Transaction t = new Transaction();
        t.setId(UUID.randomUUID().toString());
        t.setAccount(account);
        t.setAmount(amount);
        t.setTransactionType(TransactionType.SERVICE_PAYMENT);
        t.setDate(LocalDateTime.now());
        t.setDescription(String.format("Payment %s - Ref: %s", serviceName, reference));

        Transaction saved = transactionRepository.save(t);
        registerLog("SERVICE_PAYMENT", saved.getId());
        return saved;
    }

    @Override
    public List<Transaction> getTransactionsByAccount(String accountNumber) {
        BankAccount account = bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BusinessException("Account not found"));
        return transactionRepository.findByAccountId(account.getId());
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than 0");
        }
    }

    private void registerLog(String operation, String transactionId) {
        OperationsLog log = new OperationsLog();
        log.setId(UUID.randomUUID().toString());
        log.setTimestamp(LocalDateTime.now());
        log.setOperation(operation);
        
        Map<String, String> details = new HashMap<>();
        details.put("transactionId", transactionId);
        log.setDetails(details);
        
        operationsLogRepository.save(log);
    }
}
