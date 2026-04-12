package app.domain.models;

import app.domain.Exceptions.InvalidAmountException;
import app.domain.Exceptions.InsufficientFundsException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
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

    // VALIDATION: Balance cannot be negative
    public void setCurrentBalance(BigDecimal currentBalance) {
        if (currentBalance == null) {
            throw new IllegalArgumentException("El saldo no puede ser nulo");
        }
        if (currentBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("El saldo no puede ser negativo. Valor: " + currentBalance);
        }
        this.currentBalance = currentBalance;
    }

    // BLINDED METHOD: Debit with validations
    public void debit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("El monto debe ser mayor a 0");
        }
        BigDecimal newBalance = this.currentBalance.subtract(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException(
                String.format("Saldo insuficiente. Disponible: %s, Requerido: %s",
                    this.currentBalance, amount)
            );
        }
        this.currentBalance = newBalance;
    }

    // BLINDED METHOD: Credit with validations
    public void credit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("El monto debe ser mayor a 0");
        }
        this.currentBalance = this.currentBalance.add(amount);
    }
}
