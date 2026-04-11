package app.domain.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
        if (currentBalance != null && currentBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Account balance cannot be negative");
        }
        this.currentBalance = currentBalance;
    }
}
