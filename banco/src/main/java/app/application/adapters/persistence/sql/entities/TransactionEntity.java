package app.application.adapters.persistence.sql.entities;

import app.domain.models.BankAccount;
import app.domain.models.Transaction;
import app.domain.models.TransactionType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private String description;

    public static TransactionEntity fromDomain(Transaction t) {
        if (t == null) return null;
        TransactionEntity e = new TransactionEntity();
        e.setId(t.getId());
        e.setAccountId(t.getAccount() != null ? t.getAccount().getId() : null);
        e.setAmount(t.getAmount());
        e.setTransactionType(t.getTransactionType());
        e.setDate(t.getDate());
        e.setDescription(t.getDescription());
        return e;
    }

    public Transaction toDomain() {
        Transaction t = new Transaction();
        t.setId(getId());
        BankAccount acc = new BankAccount();
        acc.setId(getAccountId());
        t.setAccount(acc);
        t.setAmount(getAmount());
        t.setTransactionType(getTransactionType());
        t.setDate(getDate());
        t.setDescription(getDescription());
        return t;
    }
}
