package app.domain.ports;

import app.domain.models.OperationsLog;
import java.util.List;

public interface OperationsLogPort {
    OperationsLog save(OperationsLog log);
    
    /**
     * CORRECCIÓN 2: Se cambia Long productId por String accountNumber 
     * para mantener consistencia con los modelos de dominio.
     */
    List<OperationsLog> findByAffectedProductAccountNumber(String accountNumber);
}
