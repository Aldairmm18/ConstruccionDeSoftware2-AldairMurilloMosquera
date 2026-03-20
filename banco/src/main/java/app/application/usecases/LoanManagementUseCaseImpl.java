package app.application.usecases;

import app.domain.models.Loan;
import app.domain.models.LoanStatus;
import app.domain.ports.LoanPort;
import app.domain.services.LoanDomainService;
import app.domain.Exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanManagementUseCaseImpl implements LoanManagementUseCase {

    private final LoanPort loanPort;
    private final LoanDomainService loanDomainService;

    @Override
    public Loan requestLoan(Loan loan) {
        loanDomainService.validateLoanCreation(loan);
        loan.setLoanStatus(LoanStatus.UNDER_REVIEW);
        return loanPort.save(loan);
    }

    @Override
    public Loan approveLoan(Long loanId) {
        Loan loan = loanPort.findById(loanId);
        if (loan == null) {
            throw new BusinessException("Préstamo no encontrado.");
        }
        loan.setLoanStatus(LoanStatus.APPROVED);
        return loanPort.save(loan);
    }
}
