package app.domain.models;

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
public class Transfer {
    
    @Id
    private String id;
    
    @ManyToOne
    @JoinColumn(name = "origin_account_id", nullable = false)
    private BankAccount originAccount;
    
    @ManyToOne
    @JoinColumn(name = "destination_account_id", nullable = false)
    private BankAccount destinationAccount;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    
    private LocalDateTime date;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime expirationDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus status;
    
    // Constructor with expiration logic
    public Transfer(BankAccount originAccount, BankAccount destinationAccount, BigDecimal amount) {
        this.originAccount = originAccount;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
        this.createdAt = LocalDateTime.now();
        this.expirationDate = this.createdAt.plusMinutes(60);
        this.status = TransferStatus.PENDING;
    }
    
    // Method to check expiration
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationDate);
    }
}
