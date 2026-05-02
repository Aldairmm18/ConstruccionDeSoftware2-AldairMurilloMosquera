package app.application.adapters.persistence.sql.entities;

import app.domain.models.OperationsLog;
import app.domain.models.SystemRole;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "operations_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OperationsLogEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String logId;
  private String operationType;
  private LocalDateTime operationDateTime;

  // CORRECCIÓN 2: Referencias reales a entidades JPA
  @ManyToOne
  @JoinColumn(name = "affected_product_id")
  private BankAccountEntity affectedProduct;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserEntity user;

  @Enumerated(EnumType.STRING)
  private SystemRole userRole;

  @Transient
  private Map<String, Object> detailData = new HashMap<>();

  public static OperationsLogEntity fromDomain(OperationsLog log) {
    if (log == null) {
      return null;
    }
    OperationsLogEntity entity = new OperationsLogEntity();
    // id is String in domain but Long in SQL — parse only if numeric
    if (log.getId() != null) {
      try {
        entity.setId(Long.parseLong(log.getId()));
      } catch (NumberFormatException e) {
        // MongoDB ObjectId or UUID — let SQL generate its own id
        entity.setId(null);
      }
    }
    entity.setLogId(log.getLogId());
    entity.setOperationType(log.getOperationType());
    entity.setOperationDateTime(log.getOperationDateTime());
    
    // Mapeo de objetos de dominio a entidades
    entity.setAffectedProduct(BankAccountEntity.fromDomain(log.getAffectedProduct()));
    entity.setUser(UserEntity.fromDomain(log.getUser()));
    
    entity.setUserRole(log.getUserRole());
    entity.setDetailData(log.getDetailData());
    return entity;
  }

  public OperationsLog toDomain() {
    OperationsLog log = new OperationsLog();
    // Convert Long SQL id to String for domain model
    log.setId(getId() != null ? String.valueOf(getId()) : null);
    log.setLogId(getLogId());
    log.setOperationType(getOperationType());
    log.setOperationDateTime(getOperationDateTime());
    
    // Mapeo de entidades a objetos de dominio
    log.setAffectedProduct(getAffectedProduct() != null ? getAffectedProduct().toDomain() : null);
    log.setUser(getUser() != null ? getUser().toDomain() : null);
    
    log.setUserRole(getUserRole());
    log.setDetailData(getDetailData());
    return log;
  }
}
