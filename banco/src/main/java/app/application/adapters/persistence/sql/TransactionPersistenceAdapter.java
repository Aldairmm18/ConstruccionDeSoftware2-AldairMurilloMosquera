package app.application.adapters.persistence.sql;

import app.application.adapters.persistence.sql.entities.TransactionEntity;
import app.application.adapters.persistence.sql.repositories.TransactionRepository;
import app.domain.models.Transaction;
import app.domain.ports.TransactionPort;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionPersistenceAdapter implements TransactionPort {

    private final TransactionRepository transactionRepository;

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity entity = TransactionEntity.fromDomain(transaction);
        if (entity != null && (entity.getId() == null || entity.getId() == 0L)) {
            entity.setId(null);
        }
        return transactionRepository.save(entity).toDomain();
    }

    @Override
    public Optional<Transaction> findById(Long id) {
        return transactionRepository.findById(id).map(TransactionEntity::toDomain);
    }

    @Override
    public List<Transaction> findAll() {
        return transactionRepository.findAll().stream()
                .map(TransactionEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByAccountId(Long accountId) {
        return transactionRepository.findByAccountId(accountId).stream()
                .map(TransactionEntity::toDomain)
                .collect(Collectors.toList());
    }
}
