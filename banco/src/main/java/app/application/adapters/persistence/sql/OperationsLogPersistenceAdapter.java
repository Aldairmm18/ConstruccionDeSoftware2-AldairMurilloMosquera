package app.application.adapters.persistence.sql;

import app.application.adapters.persistence.sql.entities.OperationsLogEntity;
import app.application.adapters.persistence.sql.repositories.OperationsLogRepository;
import app.domain.models.OperationsLog;
import app.domain.ports.OperationsLogPort;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Adaptador de persistencia para el log de operaciones.
 * Implementa las correcciones requeridas para referencias de dominio.
 */
@Component
@RequiredArgsConstructor
public class OperationsLogPersistenceAdapter implements OperationsLogPort {

    private final OperationsLogRepository repository;

    @Override
    public OperationsLog save(OperationsLog log) {
        OperationsLogEntity entity = OperationsLogEntity.fromDomain(log);
        OperationsLogEntity saved = repository.save(entity);
        return saved.toDomain();
    }

    @Override
    public List<OperationsLog> findByProductId(Long productId) {
        // Implementación del nuevo método usando el repositorio
        return repository.findByAffectedProductId(productId)
                .stream()
                .map(OperationsLogEntity::toDomain)
                .collect(Collectors.toList());
    }
}
