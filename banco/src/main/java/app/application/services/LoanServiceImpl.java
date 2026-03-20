package app.application.services;

import app.domain.models.Loan;
import app.domain.ports.LoanPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

  private final LoanPort loanPort;

  @Override
  public Loan create(Loan loan) {
    return loanPort.save(loan);
  }

  @Override
  public Loan findById(Long id) {
    return loanPort.findById(id);
  }

  @Override
  public List<Loan> findAll() {
    return loanPort.findAll();
  }
}
