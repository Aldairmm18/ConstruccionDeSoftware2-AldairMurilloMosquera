package app.domain.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
public class BankAccount {

    private String accountNumber;
    private AccountType accountType;
    private String holderId;
    private double currentBalance;
    private Currency currency;
    private AccountStatus accountStatus;
    private Date openingDate;
}
