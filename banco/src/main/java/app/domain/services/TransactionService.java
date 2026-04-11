package app.domain.services;

import app.domain.Exceptions.BusinessException;
import app.domain.Exceptions.InsufficientFundsException;
import app.domain.Exceptions.InvalidAmountException;
import app.domain.models.BankAccount;
import app.domain.ports.BankAccountPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final BankAccountPort bankAccountPort;


    @Transactional
    public void deposit(String accountNumber, BigDecimal amount) {
        validateAmount(amount);

        BankAccount account = bankAccountPort.findByAccountNumberForUpdate(accountNumber);
        validateExistence(account);

        account.setCurrentBalance(account.getCurrentBalance().add(amount));
        bankAccountPort.save(account);
    }


    @Transactional
    public void withdraw(String accountNumber, BigDecimal amount) {
        validateAmount(amount);

        BankAccount account = bankAccountPort.findByAccountNumberForUpdate(accountNumber);
        validateExistence(account);
        validateSufficientBalance(account, amount);

        account.setCurrentBalance(account.getCurrentBalance().subtract(amount));
        bankAccountPort.save(account);
    }


    @Transactional
    public void transfer(String sourceNumber, String targetNumber, BigDecimal amount) {
        validateAmount(amount);
        
        if (sourceNumber.equals(targetNumber)) {
            throw new BusinessException("Source and target accounts must be different");
        }

        BankAccount source = bankAccountPort.findByAccountNumberForUpdate(sourceNumber);
        BankAccount target = bankAccountPort.findByAccountNumberForUpdate(targetNumber);

        validateExistence(source);
        validateExistence(target);
        validateSufficientBalance(source, amount);

        source.setCurrentBalance(source.getCurrentBalance().subtract(amount));
        target.setCurrentBalance(target.getCurrentBalance().add(amount));

        bankAccountPort.save(source);
        bankAccountPort.save(target);
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
            throw new InsufficientFundsException("Insufficient balance to complete the operation");
        }
    }

}
