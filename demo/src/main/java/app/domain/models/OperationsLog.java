package app.domain.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class OperationsLog {

    private String logId;
    private String operationType;
    private LocalDateTime operationDateTime;
    private long userId;
    private String userRole;
    private String affectedProductId;
    private Map<String, Object> detailData;
}
