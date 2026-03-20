package app.infrastructure.persistence;

import app.domain.models.Transfer;
import app.domain.ports.TransferPort;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class TransferRepository extends SimpleJpaRepository<TransferEntity, Long>
    implements TransferPort {

  public TransferRepository(EntityManager entityManager) {
    super(TransferEntity.class, entityManager);
  }

  @Override
  public Transfer save(Transfer transfer) {
    TransferEntity entity = TransferEntity.fromDomain(transfer);
    if (entity != null && (entity.getTransferId() == null || entity.getTransferId() == 0L)) {
      entity.setTransferId(null);
    }
    TransferEntity saved = super.save(entity);
    return saved != null ? saved.toDomain() : null;
  }

  @Override
  public Transfer findById(Long id) {
    return super.findById(id).map(TransferEntity::toDomain).orElse(null);
  }

  @Override
  public List<Transfer> findAll() {
    return super.findAll().stream().map(TransferEntity::toDomain).toList();
  }
}
