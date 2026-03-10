package app.domain.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
public class Transfer {

    private long transferId;
    private String sourceAccount;
    private String targetAccount;
    private double amount;
    private Timestamp creationDate;
    private Timestamp approvalDate;
    private TransferStatus transferStatus;
    private long creatorUserId;
    private Long approverUserId;
}
