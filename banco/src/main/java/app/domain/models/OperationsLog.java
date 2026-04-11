package app.domain.models;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
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
  
  // CORRECCIÓN 2: Referencias a entidades de dominio en lugar de IDs primitivos
  private BankAccount affectedProduct;
  private User user;
  
  private SystemRole userRole;
  private Map<String, Object> detailData = new HashMap<>();
}
