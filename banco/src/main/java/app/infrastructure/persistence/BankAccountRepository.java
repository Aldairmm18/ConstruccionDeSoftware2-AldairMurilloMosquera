package app.infrastructure.persistence;

import app.domain.models.BankAccount;
import app.domain.ports.BankAccountPort;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class BankAccountRepository extends SimpleJpaRepository<BankAccountEntity, Long>
    implements BankAccountPort {

  public BankAccountRepository(EntityManager entityManager) {
    super(BankAccountEntity.class, entityManager);
  }

  @Override
  public BankAccount save(BankAccount bankAccount) {
    BankAccountEntity entity = BankAccountEntity.fromDomain(bankAccount);
    if (entity != null && (entity.getId() == null || entity.getId() == 0L)) {
      entity.setId(null);
    }
    BankAccountEntity saved = super.save(entity);
    return saved != null ? saved.toDomain() : null;
  }

  @Override
  public BankAccount findById(Long id) {
    return super.findById(id).map(BankAccountEntity::toDomain).orElse(null);
  }

  @Override
  public List<BankAccount> findAll() {
    return super.findAll().stream().map(BankAccountEntity::toDomain).toList();
  }
}
