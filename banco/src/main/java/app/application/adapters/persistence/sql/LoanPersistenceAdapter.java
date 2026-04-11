package app.application.adapters.persistence.sql;

import app.application.adapters.persistence.sql.entities.LoanEntity;
import app.application.adapters.persistence.sql.repositories.LoanRepository;
import app.domain.models.Loan;
import app.domain.ports.LoanPort;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanPersistenceAdapter implements LoanPort {

  private final LoanRepository loanRepository;

  @Override
  public Loan save(Loan loan) {
    LoanEntity entity = LoanEntity.fromDomain(loan);
    if (entity != null && (entity.getLoanId() == null || entity.getLoanId() == 0L)) {
      entity.setLoanId(null);
    }
    LoanEntity saved = loanRepository.save(entity);
    return saved.toDomain();
  }

  @Override
  public Loan findById(Long id) {
    return loanRepository.findById(id).map(LoanEntity::toDomain).orElse(null);
  }

  @Override
  public List<Loan> findAll() {
    return loanRepository.findAll().stream().map(LoanEntity::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Loan> findByRequestingClientId(Long requestingClientId) {
    return loanRepository.findByRequestingClientId(requestingClientId).stream().map(LoanEntity::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Loan> findByLoanStatus(app.domain.models.LoanStatus loanStatus) {
    return loanRepository.findByLoanStatus(loanStatus).stream().map(LoanEntity::toDomain).collect(Collectors.toList());
  }
}
