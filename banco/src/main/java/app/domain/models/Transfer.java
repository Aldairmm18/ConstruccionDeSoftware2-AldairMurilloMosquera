package app.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transfer {

    private Long id;

    private BankAccount sourceAccount;

    private BankAccount targetAccount;

    private BigDecimal amount;

    private TransferStatus transferStatus;

    private LocalDateTime creationDate;

    private LocalDateTime approvalDate;

    private User creatorUser;

    private User approverUser;

    private LocalDateTime expirationDate;

    // Constructor with expiration logic
    public Transfer(BankAccount sourceAccount, BankAccount targetAccount, BigDecimal amount) {
        this.sourceAccount = sourceAccount;
        this.targetAccount = targetAccount;
        this.amount = amount;
        this.creationDate = LocalDateTime.now();
        this.expirationDate = this.creationDate.plusMinutes(60);
        this.transferStatus = TransferStatus.PENDING;
    }

    // Method to check expiration
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationDate);
    }
}
