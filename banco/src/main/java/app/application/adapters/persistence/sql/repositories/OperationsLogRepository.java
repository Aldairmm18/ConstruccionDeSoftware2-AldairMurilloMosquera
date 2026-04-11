package app.application.adapters.persistence.sql.repositories;

import app.application.adapters.persistence.sql.entities.OperationsLogEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationsLogRepository extends JpaRepository<OperationsLogEntity, Long> {

    // CORRECCIÓN 2: Consulta por ID de producto afectado
    @Query("SELECT o FROM OperationsLogEntity o WHERE o.affectedProduct.id = :productId")
    List<OperationsLogEntity> findByAffectedProductId(@Param("productId") Long productId);
}
