package app.application.adapters.persistence.nosql.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "operations_log")
public class OperationsLogDocument {

    @Id
    private String id;

    @Indexed
    private String operationType;

    private LocalDateTime operationDateTime;

    @Indexed
    private Long userId;

    private String userRole;

    @Indexed
    private String affectedProductId;

    private Map<String, Object> detailData;
}
