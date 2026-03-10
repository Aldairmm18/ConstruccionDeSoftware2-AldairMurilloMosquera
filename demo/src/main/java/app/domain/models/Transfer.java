package app.domain.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Transfer {

    private long transferId;
    private String sourceAccount;
    private String targetAccount;
    private double amount;
    private LocalDateTime creationDate;
    private LocalDateTime approvalDate;
    private TransferStatus transferStatus;
    private long creatorUserId;
    private Long approverUserId;
}
