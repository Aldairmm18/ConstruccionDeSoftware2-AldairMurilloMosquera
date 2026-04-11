package app.domain.ports;

import app.domain.models.OperationsLog;
import java.util.List;

public interface OperationsLogPort {
    OperationsLog save(OperationsLog log);
    
    // Nueva funcionalidad requerida
    List<OperationsLog> findByProductId(Long productId);
}
