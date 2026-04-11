package app.domain.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BankAccount {

    private Long id;
    
    private String accountNumber;
    
    private AccountType accountType;
    
    private AccountStatus accountStatus;
    
    private Currency currency;
    
    private BigDecimal currentBalance;
    
    private LocalDate openingDate;
    
    private PersonClient client;


    public BankAccount(Long id, String accountNumber, AccountType accountType, AccountStatus accountStatus, 
                       Currency currency, BigDecimal currentBalance, LocalDate openingDate, PersonClient client) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.accountStatus = accountStatus;
        this.currency = currency;
        this.openingDate = openingDate;
        this.client = client;
        setCurrentBalance(currentBalance);
    }


    public void setCurrentBalance(BigDecimal currentBalance) {
        if (currentBalance != null && currentBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Account balance cannot be negative");
        }
        this.currentBalance = currentBalance;
    }

}
