package app.application.adapters.persistence.sql.repositories;

import app.application.adapters.persistence.sql.entities.OperationsLogEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationsLogRepository extends JpaRepository<OperationsLogEntity, Long> {

    /**
     * CORRECCIÓN 2: Consulta por número de cuenta del producto afectado.
     * Se realiza un join implícito con BankAccountEntity.
     */
    @Query("SELECT o FROM OperationsLogEntity o WHERE o.affectedProduct.accountNumber = :accountNumber")
    List<OperationsLogEntity> findByAffectedProductAccountNumber(@Param("accountNumber") String accountNumber);
}
