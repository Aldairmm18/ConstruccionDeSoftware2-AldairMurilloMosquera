package app.domain.services;

import app.domain.Exceptions.*;
import app.domain.models.*;
import app.domain.ports.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for LOAN OPERATIONS
 * Handles: Loan requests, approvals, rejections, and disbursements
 */
@Service
public class LoanService {
    
    @Autowired
    private LoanRepository loanRepository;
    
    @Autowired
    private ClientRepository clientRepository;
    
    @Autowired
    private BankAccountRepository accountRepository;
    
    @Autowired
    private AuditService auditService;
    
    private static final int MINIMUM_ACCOUNTS_FOR_LOAN = 2;
    
    // ==================== CREATE OPERATIONS ====================
    
    /**
     * Requests a new loan
     * RULE: Client must have at least 2 active accounts
     */
    @Transactional
    public Loan requestLoan(
            String clientId,
            BigDecimal amount,
            BigDecimal interestRate,
            int termMonths,
            String disbursementAccountId) {
        
        Client client = findClientOrThrow(clientId);
        validateMinimumAccounts(clientId);
        validateLoanAmount(amount);
        
        BankAccount disbursementAccount = validateDisbursementAccount(
            disbursementAccountId, clientId);
        
        Loan loan = new Loan();
        loan.setId(UUID.randomUUID().toString());
        loan.setClient(client);
        loan.setAmount(amount);
        loan.setInterestRate(interestRate);
        loan.setTermMonths(termMonths);
        loan.setRequestDate(LocalDateTime.now());
        loan.setStatus(LoanStatus.PENDING);
        loan.setDisbursementAccount(disbursementAccount);
        
        Loan saved = loanRepository.save(loan);
        auditService.logOperation("LOAN_REQUESTED", saved.getId());
        
        return saved;
    }
    
    // ==================== APPROVAL OPERATIONS ====================
    
    /**
     * Approves a loan request
     * Only ADMINISTRATORS can approve loans
     */
    @Transactional
    public Loan approveLoan(String loanId, String adminId) {
        Loan loan = findLoanOrThrow(loanId);
        
        validateSolicitadoStatus(loan);
        
        loan.setStatus(LoanStatus.APPROVED);
        Loan updated = loanRepository.save(loan);
        
        auditService.logOperation("LOAN_APPROVED", updated.getId());
        
        return updated;
    }
    
    /**
     * Rejects a loan request
     */
    @Transactional
    public Loan rejectLoan(String loanId, String reason) {
        Loan loan = findLoanOrThrow(loanId);
        
        validateSolicitadoStatus(loan);
        
        loan.setStatus(LoanStatus.REJECTED);
        Loan updated = loanRepository.save(loan);
        
        auditService.logOperation("LOAN_REJECTED", updated.getId());
        
        return updated;
    }
    
    // ==================== DISBURSEMENT OPERATIONS ====================
    
    /**
     * Disburses an approved loan
     * Credits the loan amount to the disbursement account
     */
    @Transactional
    public Loan disburseLoan(String loanId) {
        Loan loan = findLoanOrThrow(loanId);
        
        validateApprovedStatus(loan);
        
        BankAccount account = accountRepository.findById(loan.getDisbursementAccount().getId())
            .orElseThrow(() -> new BusinessException(
                "Cuenta de desembolso no encontrada"));
        
        account.credit(loan.getAmount());
        accountRepository.save(account);
        
        loan.setStatus(LoanStatus.DISBURSED);
        Loan updated = loanRepository.save(loan);
        
        auditService.logOperation("LOAN_DISBURSED", updated.getId());
        
        return updated;
    }
    
    // ==================== QUERY OPERATIONS ====================
    
    public List<Loan> findClientLoans(String clientId) {
        return loanRepository.findByClientId(clientId);
    }
    
    public Optional<Loan> findById(String id) {
        return loanRepository.findById(id);
    }
    
    public List<Loan> findAll() {
        return loanRepository.findAll();
    }
    
    // ==================== VALIDATION METHODS ====================
    
    private Client findClientOrThrow(String clientId) {
        return clientRepository.findById(clientId)
            .orElseThrow(() -> new UserNotFoundException(
                "Cliente no encontrado: " + clientId));
    }
    
    private Loan findLoanOrThrow(String loanId) {
        return loanRepository.findById(loanId)
            .orElseThrow(() -> new BusinessException(
                "Préstamo no encontrado: " + loanId));
    }
    
    private void validateMinimumAccounts(String clientId) {
        long activeAccounts = accountRepository.countByClientId(clientId);
        if (activeAccounts < MINIMUM_ACCOUNTS_FOR_LOAN) {
            throw new LoanRejectedException(
                String.format("Debe tener al menos %d cuentas activas. Tiene: %d",
                    MINIMUM_ACCOUNTS_FOR_LOAN, activeAccounts));
        }
    }
    
    private void validateLoanAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Monto del préstamo debe ser mayor a 0");
        }
    }
    
    private BankAccount validateDisbursementAccount(String accountId, String clientId) {
        BankAccount account = accountRepository.findById(accountId)
            .orElseThrow(() -> new BusinessException(
                "Cuenta de desembolso no encontrada"));
        
        if (!account.getClient().getId().equals(clientId)) {
            throw new IllegalArgumentException(
                "La cuenta de desembolso debe pertenecer al cliente");
        }
        
        return account;
    }
    
    private void validateSolicitadoStatus(Loan loan) {
        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new IllegalStateException(
                "Solo se pueden procesar préstamos en estado PENDING");
        }
    }
    
    private void validateApprovedStatus(Loan loan) {
        if (loan.getStatus() != LoanStatus.APPROVED) {
            throw new IllegalStateException(
                "Solo se pueden desembolsar préstamos APROBADOS");
        }
    }
}
