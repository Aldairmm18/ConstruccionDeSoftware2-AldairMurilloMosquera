package app.domain.services;

import app.domain.Exceptions.*;
import app.domain.models.*;
import app.domain.ports.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Service for DEPOSIT OPERATIONS
 * Handles: Cash deposits to accounts
 */
public class DepositService {

    private final BankAccountPort bankAccountPort;
    private final TransactionPort transactionPort;
    private final ValidationService validationService;
    private final AuditService auditService;

    public DepositService(BankAccountPort bankAccountPort, TransactionPort transactionPort,
                          ValidationService validationService, AuditService auditService) {
        this.bankAccountPort = bankAccountPort;
        this.transactionPort = transactionPort;
        this.validationService = validationService;
        this.auditService = auditService;
    }

    /**
     * Executes a cash deposit to an account
     */
    public Transaction executeDeposit(
            String accountNumber,
            BigDecimal amount,
            String description) {

        validationService.validateAmount(amount);

        BankAccount account = bankAccountPort.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new BusinessException("Cuenta no encontrada: " + accountNumber);
        }

        BigDecimal previousBalance = account.getCurrentBalance();

        account.credit(amount);
        bankAccountPort.save(account);

        Transaction transaction = createTransaction(
            account, amount, TransactionType.DEPOSIT, description);

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
            description != null ? description : "Depósito en efectivo");

        return transaction;
    }
}
