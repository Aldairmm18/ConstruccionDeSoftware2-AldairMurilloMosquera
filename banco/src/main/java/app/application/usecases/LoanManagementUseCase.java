package app.application.usecases;

import app.domain.models.Loan;
import java.util.List;

public interface LoanManagementUseCase {
    Loan requestLoan(Loan loan);
    Loan approveLoan(Long loanId, Long userId);
    Loan disburseLoan(Long loanId);
    List<Loan> findAll();
    Loan findById(Long id);
    List<Loan> findByClientId(Long clientId);
}
