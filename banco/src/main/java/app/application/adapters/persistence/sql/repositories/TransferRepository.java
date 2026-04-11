package app.application.adapters.persistence.sql.repositories;

import app.application.adapters.persistence.sql.entities.TransferEntity;
import app.domain.models.TransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<TransferEntity, Long> {
  
  @Query("SELECT t FROM TransferEntity t WHERE t.sourceAccount.accountNumber = :sourceAccount")
  List<TransferEntity> findBySourceAccount(@Param("sourceAccount") String sourceAccount);

  @Query("SELECT t FROM TransferEntity t WHERE t.targetAccount.accountNumber = :targetAccount")
  List<TransferEntity> findByTargetAccount(@Param("targetAccount") String targetAccount);

  List<TransferEntity> findByTransferStatus(TransferStatus transferStatus);
  List<TransferEntity> findByTransferStatusAndCreationDateBefore(TransferStatus status, java.time.LocalDateTime dateTime);
}
