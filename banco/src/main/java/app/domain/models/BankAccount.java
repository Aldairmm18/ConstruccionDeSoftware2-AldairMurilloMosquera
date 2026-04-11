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

    public void setCurrentBalance(BigDecimal currentBalance) {
        if (currentBalance == null) {
            throw new IllegalArgumentException("Saldo no puede ser nulo");
        }
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new app.domain.Exceptions.InsufficientFundsException("Saldo no puede ser negativo. Valor: " + balance);
        }
        this.currentBalance = balance;
    }

    // MÉTODO BLINDADO: Debitar con validaciones
    public void debitar(BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new app.domain.Exceptions.InvalidAmountException("Monto debe ser mayor a 0");
        }
        BigDecimal nuevoSaldo = this.currentBalance.subtract(monto);
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new app.domain.Exceptions.InsufficientFundsException(
                String.format("Saldo insuficiente. Disponible: %s, Requerido: %s", 
                    this.currentBalance, monto)
            );
        }
        this.currentBalance = nuevoSaldo;
    }

    // MÉTODO BLINDADO: Acreditar con validaciones
    public void acreditar(BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new app.domain.Exceptions.InvalidAmountException("Monto debe ser mayor a 0");
        }
        if (this.currentBalance == null) {
            this.currentBalance = BigDecimal.ZERO;
        }
        this.currentBalance = this.currentBalance.add(monto);
    }
}
