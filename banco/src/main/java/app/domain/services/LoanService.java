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
            throw new BusinessException("Datos del cliente incompletos");
        }
        
        long activeAccounts = bankAccountPort.countByClientId(loan.getClient().getId());
        if (activeAccounts < 2) {
            throw new LoanRejectedException(
                String.format("Debe tener al menos 2 cuentas activas para solicitar un préstamo. Tiene: %d", activeAccounts));
        }
        
        if (loan.getRequestedAmount() == null || loan.getRequestedAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new app.domain.Exceptions.InvalidAmountException("El monto del préstamo debe ser mayor a 0");
        }
        
        BankAccount disbursementAccount = bankAccountPort.findByAccountNumber(loan.getDisbursementTargetAccount().getAccountNumber());
        if (disbursementAccount == null) {
            throw new BusinessException("Cuenta de desembolse no encontrada");
        }
        
        if (!disbursementAccount.getClient().getId().equals(loan.getClient().getId())) {
            throw new IllegalArgumentException("La cuenta de desembolse debe pertenecer al cliente");
        }
        
        loan.setLoanStatus(LoanStatus.UNDER_REVIEW);
        Loan saved = loanPort.save(loan);
        recordLog("PRESTAMO_SOLICITADO", saved, null);
        
        return saved;
    }
    
    @Transactional
    public Loan approveLoan(Long loanId, Long userId) {
        Loan loan = loanPort.findById(loanId);
        if (loan == null) throw new BusinessException("Préstamo no encontrado");
        
        if (loan.getLoanStatus() != LoanStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Solo se pueden aprobar préstamos EN REVISIÓN");
        }
        
        loan.setLoanStatus(LoanStatus.APPROVED);
        Loan saved = loanPort.save(loan);
        recordLog("PRESTAMO_APROBADO", saved, String.valueOf(userId));
        
        return saved;
    }
    
    @Transactional
    public Loan disburseLoan(Long loanId) {
        Loan loan = loanPort.findById(loanId);
        if (loan == null) throw new BusinessException("Préstamo no encontrado");
        
        if (loan.getLoanStatus() != LoanStatus.APPROVED) {
            throw new IllegalStateException("Solo se pueden desembolsar préstamos APROBADOS");
        }
        
        transactionService.deposit(
            loan.getDisbursementTargetAccount().getAccountNumber(), 
            loan.getRequestedAmount()
        );
        
        loan.setLoanStatus(LoanStatus.DISBURSED);
        loan.setDisbursementDate(LocalDate.now());
        
        Loan updated = loanPort.save(loan);
        recordLog("PRESTAMO_DESEMBOLSADO", updated, null);
        
        return updated;
    }
    
    public List<Loan> findAll() {
        return loanPort.findAll();
    }
    
    public List<Loan> findByRequestingClientId(Long requestingClientId) {
        return loanPort.findByRequestingClientId(requestingClientId);
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
