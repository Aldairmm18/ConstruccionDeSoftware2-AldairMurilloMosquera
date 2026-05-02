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
    public List<OperationsLog> findByAffectedProductAccountNumber(String accountNumber) {
        return repository.findByAffectedProductAccountNumber(accountNumber)
                .stream()
                .map(OperationsLogEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<OperationsLog> findByAffectedProductId(String productId) {
        // SQL delegates to accountNumber-based search
        return findByAffectedProductAccountNumber(productId);
    }

    @Override
    public List<OperationsLog> findByUserId(Long userId) {
        // MongoDB is @Primary for OperationsLog; this SQL fallback is not the active implementation
        return java.util.Collections.emptyList();
    }
}
