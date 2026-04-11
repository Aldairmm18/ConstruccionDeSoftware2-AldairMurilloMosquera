package app.domain.services;

import app.domain.Exceptions.BusinessException;
import app.domain.models.Loan;
import app.domain.models.LoanStatus;
import app.domain.ports.LoanPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Consolidated Loan Service.
 * Manages the full lifecycle of loans from application to disbursement.
 */
@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanPort loanPort;
    private final LoanDomainService loanDomainService;
    private final TransactionService transactionService;

    @Transactional
    public Loan requestLoan(Loan loan) {
        // Business Rules
        loanDomainService.validateLoanCreation(loan);
        
        loan.setLoanStatus(LoanStatus.UNDER_REVIEW);
        return loanPort.save(loan);
    }

    @Transactional
    public Loan approveAndDisburse(Long loanId) {
        Loan loan = loanPort.findById(loanId);
        if (loan == null) {
            throw new BusinessException("Loan not found");
        }
        
        if (loan.getLoanStatus() != LoanStatus.UNDER_REVIEW) {
            throw new BusinessException("Only loans under review can be approved");
        }

        if (loan.getDisbursementTargetAccount() == null) {
            throw new BusinessException("A target account is required for disbursement");
        }

        // Processing
        loan.setLoanStatus(LoanStatus.APPROVED);
        loan.setApprovalDate(LocalDate.now());
        loan.setDisbursementDate(LocalDate.now());
        
        // Automatic Disbursement using TransactionService
        transactionService.deposit(
            loan.getDisbursementTargetAccount().getAccountNumber(), 
            loan.getRequestedAmount()
        );

        return loanPort.save(loan);
    }

    public Loan findById(Long id) {
        return loanPort.findById(id);
    }

    public List<Loan> findAll() {
        return loanPort.findAll();
    }
}
