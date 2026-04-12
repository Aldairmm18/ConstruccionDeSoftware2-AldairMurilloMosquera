package app.domain.ports;

import app.domain.models.Transaction;
import java.util.List;
import java.util.Optional;

public interface TransactionPort {
    Transaction save(Transaction transaction);
    Optional<Transaction> findById(Long id);
    List<Transaction> findAll();
    List<Transaction> findByAccountId(Long accountId);
}
