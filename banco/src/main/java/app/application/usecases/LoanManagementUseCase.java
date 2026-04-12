package app.application.usecases;

import app.domain.models.Loan;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface LoanManagementUseCase {
    Loan requestLoan(String clientId, BigDecimal amount, BigDecimal interestRate, int termMonths, String disbursementAccountId);
    Loan approveLoan(String loanId, String adminId);
    Loan rejectLoan(String loanId, String reason);
    Loan disburseLoan(String loanId);
    List<Loan> findLoansByClient(String clientId);
    Optional<Loan> findById(String id);
    List<Loan> findAll();
}
