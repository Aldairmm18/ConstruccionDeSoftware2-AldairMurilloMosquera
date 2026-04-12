package app.domain.models;

import app.domain.Exceptions.InvalidAmountException;
import app.domain.Exceptions.InsufficientFundsException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {
    
    @Id
    private String id;
    
    @Column(unique = true, nullable = false)
    private String accountNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    
    // VALIDATION: Balance cannot be negative
    public void setBalance(BigDecimal balance) {
        if (balance == null) {
            throw new IllegalArgumentException("El saldo no puede ser nulo");
        }
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("El saldo no puede ser negativo. Valor: " + balance);
        }
        this.balance = balance;
    }
    
    // BLINDED METHOD: Debit with validations
    public void debit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("El monto debe ser mayor a 0");
        }
        BigDecimal newBalance = this.balance.subtract(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException(
                String.format("Saldo insuficiente. Disponible: %s, Requerido: %s", 
                    this.balance, amount)
            );
        }
        this.balance = newBalance;
    }
    
    // BLINDED METHOD: Credit with validations
    public void credit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("El monto debe ser mayor a 0");
        }
        this.balance = this.balance.add(amount);
    }
}
