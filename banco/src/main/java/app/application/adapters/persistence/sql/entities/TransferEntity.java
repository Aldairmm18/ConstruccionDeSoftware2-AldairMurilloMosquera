package app.application.adapters.persistence.sql.entities;

import app.domain.models.Transfer;
import app.domain.models.TransferStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transfer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long transferId;

  @ManyToOne
  @JoinColumn(name = "source_account", nullable = false)
  private BankAccountEntity sourceAccount;

  @ManyToOne
  @JoinColumn(name = "target_account", nullable = false)
  private BankAccountEntity targetAccount;

  @Column(name = "amount", nullable = false)
  private Double amount;

  @Column(name = "transfer_status", nullable = false)
  @Enumerated(EnumType.STRING)
  private TransferStatus transferStatus;

  @Column(name = "creation_date")
  private LocalDateTime creationDate;

  @Column(name = "approval_date")
  private LocalDateTime approvalDate;

  @Column(name = "creator_user_id")
  private Long creatorUserId;

  @Column(name = "approver_user_id")
  private Long approverUserId;

  public static TransferEntity fromDomain(Transfer transfer) {
    if (transfer == null) {
      return null;
    }
    TransferEntity entity = new TransferEntity();
    entity.setTransferId(transfer.getTransferId());
    entity.setSourceAccount(BankAccountEntity.fromDomain(transfer.getSourceAccount()));
    entity.setTargetAccount(BankAccountEntity.fromDomain(transfer.getTargetAccount()));
    entity.setAmount(transfer.getAmount());
    entity.setTransferStatus(transfer.getTransferStatus());
    entity.setCreationDate(transfer.getCreationDate());
    entity.setApprovalDate(transfer.getApprovalDate());
    entity.setCreatorUserId(transfer.getCreatorUserId());
    entity.setApproverUserId(transfer.getApproverUserId());
    return entity;
  }

  public Transfer toDomain() {
    Transfer transfer = new Transfer();
    transfer.setTransferId(getTransferId());
    transfer.setSourceAccount(getSourceAccount() != null ? getSourceAccount().toDomain() : null);
    transfer.setTargetAccount(getTargetAccount() != null ? getTargetAccount().toDomain() : null);
    transfer.setAmount(getAmount());
    transfer.setTransferStatus(getTransferStatus());
    transfer.setCreationDate(getCreationDate());
    transfer.setApprovalDate(getApprovalDate());
    transfer.setCreatorUserId(getCreatorUserId());
    transfer.setApproverUserId(getApproverUserId());
    return transfer;
  }
}
