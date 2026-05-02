package app.application.adapters.persistence.nosql;

import app.application.adapters.persistence.nosql.entities.OperationsLogDocument;
import app.application.adapters.persistence.nosql.repositories.OperationsLogMongoRepository;
import app.domain.models.OperationsLog;
import app.domain.ports.OperationsLogPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Primary
@RequiredArgsConstructor
public class OperationsLogMongoAdapter implements OperationsLogPort {

    private final OperationsLogMongoRepository mongoRepository;

    @Override
    public OperationsLog save(OperationsLog log) {
        OperationsLogDocument doc = toDocument(log);
        OperationsLogDocument saved = mongoRepository.save(doc);
        return toDomain(saved);
    }

    @Override
    public List<OperationsLog> findByAffectedProductId(String productId) {
        return mongoRepository.findByAffectedProductId(productId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<OperationsLog> findByAffectedProductAccountNumber(String accountNumber) {
        // In MongoDB the affectedProductId stores the account number
        return findByAffectedProductId(accountNumber);
    }

    @Override
    public List<OperationsLog> findByUserId(Long userId) {
        return mongoRepository.findByUserId(userId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    // ==================== Conversión Domain ↔ Document ====================

    private OperationsLogDocument toDocument(OperationsLog log) {
        OperationsLogDocument doc = new OperationsLogDocument();
        // logId serves as MongoDB _id when already assigned; otherwise MongoDB generates one
        doc.setId(log.getId());
        doc.setOperationType(log.getOperationType());
        doc.setOperationDateTime(log.getOperationDateTime());
        doc.setUserId(log.getUser() != null ? log.getUser().getId() : null);
        doc.setUserRole(log.getUserRole() != null ? log.getUserRole().name() : null);
        doc.setAffectedProductId(
                log.getAffectedProduct() != null ? log.getAffectedProduct().getAccountNumber() : null
        );
        doc.setDetailData(log.getDetailData());
        return doc;
    }

    private OperationsLog toDomain(OperationsLogDocument doc) {
        OperationsLog log = new OperationsLog();
        log.setId(doc.getId());
        log.setLogId(doc.getId()); // reuse the Mongo ObjectId as logId
        log.setOperationType(doc.getOperationType());
        log.setOperationDateTime(doc.getOperationDateTime());
        log.setDetailData(doc.getDetailData());
        // user and affectedProduct are stored as IDs in MongoDB; domain objects left null
        // (they can be hydrated from MySQL if needed via separate queries)
        return log;
    }
}
