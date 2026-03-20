package app.infrastructure.persistence;

import app.domain.models.Loan;
import app.domain.ports.LoanPort;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class LoanRepository extends SimpleJpaRepository<LoanEntity, Long> implements LoanPort {

  public LoanRepository(EntityManager entityManager) {
    super(LoanEntity.class, entityManager);
  }

  @Override
  public Loan save(Loan loan) {
    LoanEntity entity = LoanEntity.fromDomain(loan);
    if (entity != null && (entity.getLoanId() == null || entity.getLoanId() == 0L)) {
      entity.setLoanId(null);
    }
    LoanEntity saved = super.save(entity);
    return saved != null ? saved.toDomain() : null;
  }

  @Override
  public Loan findById(Long id) {
    return super.findById(id).map(LoanEntity::toDomain).orElse(null);
  }

  @Override
  public List<Loan> findAll() {
    return super.findAll().stream().map(LoanEntity::toDomain).toList();
  }
}
