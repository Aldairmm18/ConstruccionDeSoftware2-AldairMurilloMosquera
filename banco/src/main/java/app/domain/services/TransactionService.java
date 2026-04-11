package app.domain.services;

import app.domain.Exceptions.BusinessException;
import app.domain.Exceptions.InsufficientFundsException;
import app.domain.Exceptions.InvalidAmountException;
import app.domain.models.BankAccount;
import app.domain.models.Transfer;
import app.domain.models.TransferStatus;
import app.domain.ports.BankAccountPort;
import app.domain.ports.TransferPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Consolidated Transaction Service.
 * Handles deposits, withdrawals, and transfers with full audit trail.
 */
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final BankAccountPort bankAccountPort;
    private final TransferPort transferPort;
    private final TransferDomainService transferDomainService;

    @Transactional
    public void deposit(String accountNumber, BigDecimal amount) {
        validateAmount(amount);

        BankAccount account = bankAccountPort.findByAccountNumberForUpdate(accountNumber);
        validateExistence(account);

        account.setCurrentBalance(account.getCurrentBalance().add(amount));
        bankAccountPort.save(account);

        // Record as a system-initiated deposit
        recordTransaction(null, account, amount, "DEPOSIT");
    }

    @Transactional
    public void withdraw(String accountNumber, BigDecimal amount) {
        validateAmount(amount);

        BankAccount account = bankAccountPort.findByAccountNumberForUpdate(accountNumber);
        validateExistence(account);
        validateSufficientBalance(account, amount);

        account.setCurrentBalance(account.getCurrentBalance().subtract(amount));
        bankAccountPort.save(account);

        // Record as a system-initiated withdrawal
        recordTransaction(account, null, amount, "WITHDRAWAL");
    }

    @Transactional
    public void transfer(String sourceNumber, String targetNumber, BigDecimal amount) {
        validateAmount(amount);
        
        BankAccount source = bankAccountPort.findByAccountNumberForUpdate(sourceNumber);
        BankAccount target = bankAccountPort.findByAccountNumberForUpdate(targetNumber);

        validateExistence(source);
        validateExistence(target);
        
        // Create model for validation
        Transfer transferModel = new Transfer();
        transferModel.setSourceAccount(source);
        transferModel.setTargetAccount(target);
        transferModel.setAmount(amount);
        
        // Use domain service for validations
        transferDomainService.validateTransferCreation(transferModel);

        // Execute balances update
        source.setCurrentBalance(source.getCurrentBalance().subtract(amount));
        target.setCurrentBalance(target.getCurrentBalance().add(amount));

        bankAccountPort.save(source);
        bankAccountPort.save(target);

        // Save entry in Ledger (Transfer table)
        transferModel.setTransferStatus(TransferStatus.EXECUTED);
        transferModel.setCreationDate(LocalDateTime.now());
        transferModel.setApprovalDate(LocalDateTime.now());
        transferPort.save(transferModel);
    }

    private void recordTransaction(BankAccount source, BankAccount target, BigDecimal amount, String type) {
        Transfer record = new Transfer();
        record.setSourceAccount(source);
        record.setTargetAccount(target);
        record.setAmount(amount);
        record.setTransferStatus(TransferStatus.EXECUTED);
        record.setCreationDate(LocalDateTime.now());
        record.setApprovalDate(LocalDateTime.now());
        transferPort.save(record);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Transaction amount must be greater than zero");
        }
    }

    private void validateExistence(BankAccount account) {
        if (account == null) {
            throw new BusinessException("Account does not exist");
        }
    }

    private void validateSufficientBalance(BankAccount account, BigDecimal amount) {
        if (account.getCurrentBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient balance for operation");
        }
    }
}
