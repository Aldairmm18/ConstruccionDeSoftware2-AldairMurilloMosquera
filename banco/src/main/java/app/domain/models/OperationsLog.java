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
public class OperationsLog {

  private Long id;
  private String logId;
  private String operationType;
  private LocalDateTime operationDateTime;
  private Long affectedProductId;
  private Long userId;
  private SystemRole userRole;
  private Long detailDataId;
}
