package app.application.usecases;

import app.domain.models.Loan;
import app.domain.models.LoanStatus;
import app.domain.ports.LoanPort;
import app.domain.ports.BankAccountPort;
import app.domain.services.LoanDomainService;
import app.domain.Exceptions.BusinessException;
import app.domain.Exceptions.UnauthorizedAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoanManagementUseCaseImpl implements LoanManagementUseCase {

    private final LoanPort loanPort;
    
    private final BankAccountPort bankAccountPort;
    
    private final LoanDomainService loanDomainService;


    @Override
    @Transactional
    public Loan requestLoan(Loan loan) {
        if (loan.getClient() == null) throw new BusinessException("Client required");
        long activeAccounts = bankAccountPort.countByClientId(loan.getClient().getId());
        
        if (activeAccounts < 2) {
            throw new BusinessException("Customer must have at least 2 active accounts to apply for a loan");
        }
        
        loanDomainService.validateLoanCreation(loan);
        loan.setLoanStatus(LoanStatus.UNDER_REVIEW);
        
        return loanPort.save(loan);
    }


    @Override
    @Transactional
    public Loan approveLoan(Long loanId) {
        verifyAdministrativeRole();
        
        Loan loan = loanPort.findById(loanId);
        
        if (loan == null) {
            throw new BusinessException("Loan application not found");
        }
        
        if (loan.getLoanStatus() != LoanStatus.UNDER_REVIEW) {
            throw new BusinessException("Only loans UNDER_REVIEW can be approved");
        }
        
        loan.setLoanStatus(LoanStatus.APPROVED);
        
        return loanPort.save(loan);
    }


    private void verifyAdministrativeRole() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null) {
            throw new UnauthorizedAccessException("Authenticated user required");
        }
        
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        
        if (role.equals("ROLE_TELLER_EMPLOYEE")) {
            throw new UnauthorizedAccessException("TELLER_EMPLOYEE is not authorized for this administrative action");
        }
    }

}
