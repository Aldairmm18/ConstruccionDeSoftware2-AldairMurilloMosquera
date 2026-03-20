package app.domain.ports;

import app.domain.models.Loan;
import java.util.List;

public interface LoanPort {

  Loan save(Loan loan);

  Loan findById(Long id);

  List<Loan> findAll();
}
