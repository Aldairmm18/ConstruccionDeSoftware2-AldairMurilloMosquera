package app.infrastructure.persistence;

import app.domain.models.AccountStatus;
import app.domain.models.AccountType;
import app.domain.models.BankAccount;
import app.domain.models.Currency;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bank_account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String accountNumber;

  @Enumerated(EnumType.STRING)
  private AccountType accountType;

  @Enumerated(EnumType.STRING)
  private AccountStatus accountStatus;

  @Enumerated(EnumType.STRING)
  private Currency currency;

  private Double currentBalance;
  private LocalDate openingDate;

  @ManyToOne
  @JoinColumn(name = "client_id")
  private ClientEntity client;

  public static BankAccountEntity fromDomain(BankAccount account) {
    if (account == null) {
      return null;
    }
    BankAccountEntity entity = new BankAccountEntity();
    entity.setId(account.getId());
    entity.setAccountNumber(account.getAccountNumber());
    entity.setAccountType(account.getAccountType());
    entity.setAccountStatus(account.getAccountStatus());
    entity.setCurrency(account.getCurrency());
    entity.setCurrentBalance(account.getCurrentBalance());
    entity.setOpeningDate(account.getOpeningDate());
    entity.setClient(ClientEntity.fromDomain(account.getClient()));
    return entity;
  }

  public BankAccount toDomain() {
    BankAccount account = new BankAccount();
    account.setId(getId());
    account.setAccountNumber(getAccountNumber());
    account.setAccountType(getAccountType());
    account.setAccountStatus(getAccountStatus());
    account.setCurrency(getCurrency());
    account.setCurrentBalance(getCurrentBalance());
    account.setOpeningDate(getOpeningDate());
    account.setClient(getClient() != null ? getClient().toDomain() : null);
    return account;
  }
}
