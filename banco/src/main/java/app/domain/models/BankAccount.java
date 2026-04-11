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
            throw new IllegalArgumentException("El saldo no puede ser nulo");
        }
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new app.domain.Exceptions.InsufficientFundsException("El saldo no puede ser negativo. Valor: " + balance);
        }
        this.currentBalance = balance;
    }

    // MÉTODO BLINDADO: Debitar con validaciones
    public void debit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new app.domain.Exceptions.InvalidAmountException("El monto debe ser mayor a 0");
        }
        if (this.currentBalance == null) this.currentBalance = BigDecimal.ZERO;
        
        BigDecimal newBalance = this.currentBalance.subtract(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new app.domain.Exceptions.InsufficientFundsException(
                String.format("Saldo insuficiente. Disponible: %s, Requerido: %s", 
                    this.currentBalance, amount)
            );
        }
        this.currentBalance = newBalance;
    }

    // MÉTODO BLINDADO: Acreditar con validaciones
    public void credit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new app.domain.Exceptions.InvalidAmountException("El monto debe ser mayor a 0");
        }
        if (this.currentBalance == null) {
            this.currentBalance = BigDecimal.ZERO;
        }
        this.currentBalance = this.currentBalance.add(amount);
    }
}
