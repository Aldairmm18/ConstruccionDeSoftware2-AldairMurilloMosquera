package app.domain.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Transfer {

  private Long id;
  private BankAccount sourceAccount;
  private BankAccount targetAccount;
  private BigDecimal amount;
  private TransferStatus transferStatus;
  private LocalDateTime creationDate;
  private LocalDateTime approvalDate;
  private Long creatorUserId;
  private Long approverUserId;
  private LocalDateTime expirationDate;

  public Transfer() {
      this.creationDate = LocalDateTime.now();
      this.expirationDate = this.creationDate.plusMinutes(60);
      this.transferStatus = TransferStatus.PENDING;
  }

  public boolean isExpired() {
      if (this.expirationDate == null) return false;
      return LocalDateTime.now().isAfter(this.expirationDate);
  }
}
