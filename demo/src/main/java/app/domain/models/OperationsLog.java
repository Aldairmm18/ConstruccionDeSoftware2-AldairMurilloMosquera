package app.domain.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
public class OperationsLog {

    private String logId;
    private String operationType;
    private Timestamp operationDateTime;
    private long userId;
    private String userRole;
    private String affectedProductId;
    private DetailData detailData;
}
