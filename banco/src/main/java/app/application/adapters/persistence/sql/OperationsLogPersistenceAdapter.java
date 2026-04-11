package app.application.adapters.persistence.sql;

import app.application.adapters.persistence.sql.entities.OperationsLogEntity;
import app.application.adapters.persistence.sql.repositories.OperationsLogRepository;
import app.domain.models.OperationsLog;
import app.domain.ports.OperationsLogPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
}
