package app.domain.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import app.domain.Exceptions.InvalidAmountException;
import app.domain.Exceptions.InsufficientFundsException;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {

    private Long id;
    private String accountNumber;
    private AccountType accountType;
    private AccountStatus accountStatus;
    private Currency currency;
    private BigDecimal currentBalance;
    private LocalDate openingDate;
    private PersonClient client;

    public void setCurrentBalance(BigDecimal balance) {
        if (balance == null) {
            throw new IllegalArgumentException("Balance cannot be null");
        }
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new app.domain.Exceptions.InsufficientFundsException("Balance cannot be negative. Value: " + balance);
        }
        this.currentBalance = balance;
    }

    // HARDENED METHOD: Debit with validations
    public void debit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new app.domain.Exceptions.InvalidAmountException("Amount must be greater than 0");
        }
        if (this.currentBalance == null) this.currentBalance = BigDecimal.ZERO;
        
        BigDecimal newBalance = this.currentBalance.subtract(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new app.domain.Exceptions.InsufficientFundsException(
                String.format("Insufficient balance. Available: %s, Required: %s", 
                    this.currentBalance, amount)
            );
        }
        this.currentBalance = newBalance;
    }

    // HARDENED METHOD: Credit with validations
    public void credit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new app.domain.Exceptions.InvalidAmountException("Amount must be greater than 0");
        }
        if (this.currentBalance == null) {
            this.currentBalance = BigDecimal.ZERO;
        }
        this.currentBalance = this.currentBalance.add(amount);
    }
}
