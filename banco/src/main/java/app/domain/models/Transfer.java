package app.domain.models;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transfer {

  private Long transferId;
  private BankAccount sourceAccount;
  private BankAccount targetAccount;
  private Double amount;
  private TransferStatus transferStatus;
  private LocalDateTime creationDate;
  private LocalDateTime approvalDate;
  private Long creatorUserId;
  private Long approverUserId;
}
