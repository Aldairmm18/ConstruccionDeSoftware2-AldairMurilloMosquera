package app.domain.services;

import app.domain.Exceptions.BusinessException;
import app.domain.Exceptions.LoanRejectedException;
import app.domain.models.Loan;
import app.domain.models.LoanStatus;
import app.domain.models.OperationsLog;
import app.domain.models.BankAccount;
import app.domain.ports.LoanPort;
import app.domain.ports.BankAccountPort;
import app.domain.ports.OperationsLogPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanService {
    
    private final LoanPort loanPort;
    private final BankAccountPort bankAccountPort;
    private final OperationsLogPort operationsLogPort;
    private final TransactionService transactionService;
    
    @Transactional
    public Loan requestLoan(Loan loan) {
        if (loan.getClient() == null || loan.getClient().getId() == null) {
            throw new BusinessException("Client data incomplete");
        }
        
        long activeAccounts = bankAccountPort.countByClientId(loan.getClient().getId());
        if (activeAccounts < 2) {
            throw new LoanRejectedException(
                String.format("Must have at least 2 active accounts to request a loan. Has: %d", activeAccounts));
        }
        
        if (loan.getRequestedAmount() == null || loan.getRequestedAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new app.domain.Exceptions.InvalidAmountException("Loan amount must be greater than 0");
        }
        
        BankAccount disbursementAccount = bankAccountPort.findByAccountNumber(loan.getDisbursementTargetAccount().getAccountNumber());
        if (disbursementAccount == null) {
            throw new BusinessException("Disbursement account not found");
        }
        
        if (!disbursementAccount.getClient().getId().equals(loan.getClient().getId())) {
            throw new IllegalArgumentException("Disbursement account must belong to the client");
        }
        
        loan.setLoanStatus(LoanStatus.UNDER_REVIEW);
        Loan saved = loanPort.save(loan);
        recordLog("LOAN_REQUESTED", saved, null);
        
        return saved;
    }
    
    @Transactional
    public Loan approveLoan(Long loanId, Long userId) {
        Loan loan = loanPort.findById(loanId);
        if (loan == null) throw new BusinessException("Loan not found");
        
        if (loan.getLoanStatus() != LoanStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Only loans UNDER_REVIEW can be approved");
        }
        
        loan.setLoanStatus(LoanStatus.APPROVED);
        Loan saved = loanPort.save(loan);
        recordLog("LOAN_APPROVED", saved, String.valueOf(userId));
        
        return saved;
    }
    
    @Transactional
    public Loan disburseLoan(Long loanId) {
        Loan loan = loanPort.findById(loanId);
        if (loan == null) throw new BusinessException("Loan not found");
        
        if (loan.getLoanStatus() != LoanStatus.APPROVED) {
            throw new IllegalStateException("Only APPROVED loans can be disbursed");
        }
        
        transactionService.deposit(
            loan.getDisbursementTargetAccount().getAccountNumber(), 
            loan.getRequestedAmount()
        );
        
        loan.setLoanStatus(LoanStatus.DISBURSED);
        loan.setDisbursementDate(LocalDate.now());
        
        Loan updated = loanPort.save(loan);
        recordLog("LOAN_DISBURSED", updated, null);
        
        return updated;
    }
    
    public List<Loan> findAll() {
        return loanPort.findAll();
    }
    
    public Loan findById(Long id) {
        return loanPort.findById(id);
    }
    
    private void recordLog(String operation, Loan l, String userId) {
        OperationsLog log = new OperationsLog();
        log.setLogId(UUID.randomUUID().toString());
        log.setOperationDateTime(LocalDateTime.now());
        log.setOperationType(operation);
        
        Map<String, Object> details = new HashMap<>();
        details.put("loanId", l.getId());
        if (l.getClient() != null) details.put("clientId", l.getClient().getId());
        details.put("amount", l.getRequestedAmount());
        details.put("status", l.getLoanStatus().toString());
        if (userId != null) details.put("userId", userId);
        log.setDetailData(details);
        
        operationsLogPort.save(log);
    }
}
