package app.domain.ports;

import app.domain.models.OperationsLog;
import java.util.List;

public interface OperationsLogPort {
    OperationsLog save(OperationsLog log);

    /**
     * Busca logs por número de cuenta del producto afectado (alias semántico usado por SQL).
     */
    List<OperationsLog> findByAffectedProductAccountNumber(String accountNumber);

    /**
     * Busca logs por ID del producto afectado (accountNumber).
     * Usado principalmente por el adaptador MongoDB.
     */
    List<OperationsLog> findByAffectedProductId(String productId);

    /**
     * Busca logs por ID de usuario.
     */
    List<OperationsLog> findByUserId(Long userId);
}
