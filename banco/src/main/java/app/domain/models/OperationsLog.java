package app.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationsLog {

    private Long id;

    private String logId;

    private String operationType;

    private LocalDateTime operationDateTime;

    private BankAccount affectedProduct;

    private User user;

    private SystemRole userRole;

    private Map<String, Object> detailData = new HashMap<>();
}
