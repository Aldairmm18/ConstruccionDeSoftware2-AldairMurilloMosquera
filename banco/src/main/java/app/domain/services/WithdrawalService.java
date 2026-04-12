package app.domain.services;

import app.domain.Exceptions.*;
import app.domain.models.*;
import app.domain.ports.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Service for WITHDRAWAL OPERATIONS
 * Handles: Cash withdrawals from accounts
 */
public class WithdrawalService {

    private final BankAccountPort bankAccountPort;
    private final TransactionPort transactionPort;
    private final ValidationService validationService;
    private final AuditService auditService;

    public WithdrawalService(BankAccountPort bankAccountPort, TransactionPort transactionPort,
                             ValidationService validationService, AuditService auditService) {
        this.bankAccountPort = bankAccountPort;
        this.transactionPort = transactionPort;
        this.validationService = validationService;
        this.auditService = auditService;
    }

    /**
     * Executes a cash withdrawal from an account
     */
    public Transaction executeWithdrawal(
            String accountNumber,
            BigDecimal amount,
            String description) {

        validationService.validateAmount(amount);

        BankAccount account = bankAccountPort.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new BusinessException("Cuenta no encontrada: " + accountNumber);
        }

        BigDecimal previousBalance = account.getCurrentBalance();

        account.debit(amount); // Throws exception if insufficient balance
        bankAccountPort.save(account);

        Transaction transaction = createTransaction(
            account, amount, TransactionType.WITHDRAWAL, description);

        Transaction saved = transactionPort.save(transaction);
        auditService.logTransaction(saved, previousBalance, account.getCurrentBalance());

        return saved;
    }

    private Transaction createTransaction(
            BankAccount account,
            BigDecimal amount,
            TransactionType type,
            String description) {

        Transaction transaction = new Transaction();
        transaction.setId(null);
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setTransactionType(type);
        transaction.setDate(LocalDateTime.now());
        transaction.setDescription(
            description != null ? description : "Retiro en efectivo");

        return transaction;
    }
}
