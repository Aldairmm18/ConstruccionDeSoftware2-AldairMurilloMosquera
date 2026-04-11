package app.domain.services;

import app.domain.Exceptions.BusinessException;
import app.domain.Exceptions.PrestamoRechazadoException;
import app.domain.models.Loan;
import app.domain.models.LoanStatus;
import app.domain.models.OperationsLog;
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
    private final BankAccountPort bankAccountPort;
    private final OperationsLogPort operationsLogPort;

    @Transactional
    public Loan requestLoan(Loan loan) {
        if (loan.getClient() != null) {
            long cuentasActivas = bankAccountPort.countByClientId(loan.getClient().getId());
            if (cuentasActivas < 2) {
                throw new PrestamoRechazadoException(
                    String.format("Debe tener al menos 2 cuentas activas. Tiene: %d", cuentasActivas));
            }
        }

        // Business Rules
        loanDomainService.validateLoanCreation(loan);
        
        loan.setLoanStatus(LoanStatus.UNDER_REVIEW);
        Loan saved = loanPort.save(loan);
        recordLog("PRESTAMO_SOLICITADO", saved);
        return saved;
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
        
        Loan saved = loanPort.save(loan);
        recordLog("PRESTAMO_APROBADO", saved);

        // Automatic Disbursement using TransactionService
        transactionService.deposit(
            loan.getDisbursementTargetAccount().getAccountNumber(), 
            loan.getRequestedAmount()
        );

        saved.setLoanStatus(LoanStatus.ACTIVE);
        Loan active = loanPort.save(saved);
        recordLog("PRESTAMO_DESEMBOLSADO", active);
        
        return active;
    }

    public Loan findById(Long id) {
        return loanPort.findById(id);
    }

    public List<Loan> findAll() {
        return loanPort.findAll();
    }

    private void recordLog(String operation, Loan l) {
        OperationsLog log = new OperationsLog();
        log.setLogId(UUID.randomUUID().toString());
        log.setOperationDateTime(LocalDateTime.now());
        log.setOperationType(operation);
        
        Map<String, Object> details = new HashMap<>();
        details.put("prestamoId", l.getId());
        if (l.getClient() != null) details.put("clienteId", l.getClient().getId());
        details.put("monto", l.getRequestedAmount());
        details.put("estado", l.getLoanStatus().toString());
        log.setDetailData(details);
        
        operationsLogPort.save(log);
    }
}
