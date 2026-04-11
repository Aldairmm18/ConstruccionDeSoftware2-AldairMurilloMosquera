package app.application.adapters.persistence.sql.repositories;

import app.application.adapters.persistence.sql.entities.OperationsLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationsLogRepository extends JpaRepository<OperationsLogEntity, Long> {
}
