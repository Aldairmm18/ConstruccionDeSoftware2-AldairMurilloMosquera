package app.domain.services;

import app.domain.Exceptions.*;
import app.domain.models.*;
import app.domain.ports.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Service for BILL PAYMENT OPERATIONS
 * Handles: Utility bill payments
 */
public class BillPaymentService {

    private final BankAccountPort bankAccountPort;
    private final TransactionPort transactionPort;
    private final ValidationService validationService;
    private final AuditService auditService;

    public BillPaymentService(BankAccountPort bankAccountPort, TransactionPort transactionPort,
                              ValidationService validationService, AuditService auditService) {
        this.bankAccountPort = bankAccountPort;
        this.transactionPort = transactionPort;
        this.validationService = validationService;
        this.auditService = auditService;
    }

    /**
     * Pays a utility bill from an account
     */
    public Transaction payBill(
            String accountNumber,
            String serviceName,
            String reference,
            BigDecimal amount) {

        validationService.validateAmount(amount);

        BankAccount account = bankAccountPort.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new BusinessException("Cuenta no encontrada: " + accountNumber);
        }

        BigDecimal previousBalance = account.getCurrentBalance();

        account.debit(amount);
        bankAccountPort.save(account);

        String description = String.format("Pago %s - Ref: %s", serviceName, reference);

        Transaction transaction = new Transaction();
        transaction.setId(null);
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.SERVICE_PAYMENT);
        transaction.setDate(LocalDateTime.now());
        transaction.setDescription(description);

        Transaction saved = transactionPort.save(transaction);
        auditService.logTransaction(saved, previousBalance, account.getCurrentBalance());

        return saved;
    }
}
