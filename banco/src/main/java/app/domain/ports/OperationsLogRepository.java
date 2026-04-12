package app.domain.ports;

import app.domain.models.OperationsLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperationsLogRepository extends JpaRepository<OperationsLog, String> {
    List<OperationsLog> findByOperation(String operation);
}
