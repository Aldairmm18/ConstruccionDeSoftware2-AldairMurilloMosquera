package app.application.adapters.persistence.sql.repositories;

import app.application.adapters.persistence.sql.entities.LoanEntity;
import app.domain.models.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, Long> {
  List<LoanEntity> findByRequestingClientId(Long requestingClientId);
  List<LoanEntity> findByLoanStatus(LoanStatus loanStatus);
}
