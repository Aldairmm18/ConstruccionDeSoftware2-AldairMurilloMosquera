package app.application.adapters.persistence.sql.entities;

import app.domain.models.OperationsLog;
import app.domain.models.SystemRole;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
  private Long affectedProductId;
  private Long userId;

  @Enumerated(EnumType.STRING)
  private SystemRole userRole;

  @Transient
  private Map<String, Object> detailData = new HashMap<>();

  public static OperationsLogEntity fromDomain(OperationsLog log) {
    if (log == null) {
      return null;
    }
    OperationsLogEntity entity = new OperationsLogEntity();
    entity.setId(log.getId());
    entity.setLogId(log.getLogId());
    entity.setOperationType(log.getOperationType());
    entity.setOperationDateTime(log.getOperationDateTime());
    entity.setAffectedProductId(log.getAffectedProductId());
    entity.setUserId(log.getUserId());
    entity.setUserRole(log.getUserRole());
    entity.setDetailData(log.getDetailData());
    return entity;
  }

  public OperationsLog toDomain() {
    OperationsLog log = new OperationsLog();
    log.setId(getId());
    log.setLogId(getLogId());
    log.setOperationType(getOperationType());
    log.setOperationDateTime(getOperationDateTime());
    log.setAffectedProductId(getAffectedProductId());
    log.setUserId(getUserId());
    log.setUserRole(getUserRole());
    log.setDetailData(getDetailData());
    return log;
  }
}
