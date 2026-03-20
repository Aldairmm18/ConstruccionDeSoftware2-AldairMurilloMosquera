package app.application.usecases;

import app.domain.models.Loan;

public interface LoanManagementUseCase {
    Loan requestLoan(Loan loan);
    Loan approveLoan(Long loanId);
}
