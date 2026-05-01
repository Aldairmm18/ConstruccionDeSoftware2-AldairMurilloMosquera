package app.application.adapters.persistence.nosql.repositories;

import app.application.adapters.persistence.nosql.entities.OperationsLogDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OperationsLogMongoRepository extends MongoRepository<OperationsLogDocument, String> {
    List<OperationsLogDocument> findByAffectedProductId(String affectedProductId);
    List<OperationsLogDocument> findByUserId(Long userId);
    List<OperationsLogDocument> findByOperationType(String operationType);
    List<OperationsLogDocument> findByOperationDateTimeBetween(LocalDateTime from, LocalDateTime to);
}
