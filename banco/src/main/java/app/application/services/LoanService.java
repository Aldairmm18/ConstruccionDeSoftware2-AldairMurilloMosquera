package app.application.services;

import app.domain.models.Loan;
import java.util.List;

public interface LoanService {

  Loan create(Loan loan);

  Loan findById(Long id);

  List<Loan> findAll();
}
