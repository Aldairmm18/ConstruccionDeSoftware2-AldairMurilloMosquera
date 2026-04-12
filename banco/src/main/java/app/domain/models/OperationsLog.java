package app.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationsLog {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column(nullable = false)
    private String operation;
    
    @ElementCollection
    @CollectionTable(name = "operations_log_details", joinColumns = @JoinColumn(name = "log_id"))
    @MapKeyColumn(name = "detail_key")
    @Column(name = "detail_value")
    private Map<String, String> details = new HashMap<>(); // Changed to Map<String, String> for easier persistence
}
