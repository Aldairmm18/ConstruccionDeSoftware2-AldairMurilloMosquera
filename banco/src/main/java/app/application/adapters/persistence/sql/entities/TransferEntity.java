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
import java.math.BigDecimal;
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
  private BigDecimal amount;

  @Column(name = "transfer_status", nullable = false)
  @Enumerated(EnumType.STRING)
  private TransferStatus transferStatus;

  @Column(name = "creation_date")
  private LocalDateTime creationDate;

  @Column(name = "approval_date")
  private LocalDateTime approvalDate;

  // CORRECCIÓN 3: Relaciones reales con UserEntity
  @ManyToOne
  @JoinColumn(name = "creator_user_id")
  private UserEntity creatorUser;

  @ManyToOne
  @JoinColumn(name = "approver_user_id")
  private UserEntity approverUser;

  @Column(name = "expiration_date")
  private LocalDateTime expirationDate;

  public static TransferEntity fromDomain(Transfer transfer) {
    if (transfer == null) {
      return null;
    }
    TransferEntity entity = new TransferEntity();
    entity.setTransferId(transfer.getId());
    entity.setSourceAccount(BankAccountEntity.fromDomain(transfer.getSourceAccount()));
    entity.setTargetAccount(BankAccountEntity.fromDomain(transfer.getTargetAccount()));
    entity.setAmount(transfer.getAmount());
    entity.setTransferStatus(transfer.getTransferStatus());
    entity.setCreationDate(transfer.getCreationDate());
    entity.setApprovalDate(transfer.getApprovalDate());
    
    // Mapeo de objetos de dominio a entidades persistentes
    entity.setCreatorUser(UserEntity.fromDomain(transfer.getCreatorUser()));
    entity.setApproverUser(UserEntity.fromDomain(transfer.getApproverUser()));
    
    entity.setExpirationDate(transfer.getExpirationDate());
    return entity;
  }

  public Transfer toDomain() {
    Transfer transfer = new Transfer();
    transfer.setId(getTransferId());
    transfer.setSourceAccount(getSourceAccount() != null ? getSourceAccount().toDomain() : null);
    transfer.setTargetAccount(getTargetAccount() != null ? getTargetAccount().toDomain() : null);
    transfer.setAmount(getAmount());
    transfer.setTransferStatus(getTransferStatus());
    transfer.setCreationDate(getCreationDate());
    transfer.setApprovalDate(getApprovalDate());
    
    // Mapeo de entidades persistentes a objetos de dominio
    transfer.setCreatorUser(getCreatorUser() != null ? getCreatorUser().toDomain() : null);
    transfer.setApproverUser(getApproverUser() != null ? getApproverUser().toDomain() : null);
    
    transfer.setExpirationDate(getExpirationDate());
    return transfer;
  }
}
