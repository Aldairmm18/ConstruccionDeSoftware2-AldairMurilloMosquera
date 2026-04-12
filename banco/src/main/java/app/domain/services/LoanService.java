package app.domain.services;

import app.domain.Exceptions.*;
import app.domain.models.*;
import app.domain.ports.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Service for LOAN OPERATIONS
 * Handles: Loan requests, approvals, rejections, and disbursements
 */
public class LoanService {

    private final LoanPort loanPort;
    private final ClientPort clientPort;
    private final BankAccountPort bankAccountPort;
    private final AuditService auditService;

    private static final int MINIMUM_ACCOUNTS_FOR_LOAN = 2;

    public LoanService(LoanPort loanPort, ClientPort clientPort, BankAccountPort bankAccountPort,
                       AuditService auditService) {
        this.loanPort = loanPort;
        this.clientPort = clientPort;
        this.bankAccountPort = bankAccountPort;
        this.auditService = auditService;
    }

    // ==================== CREATE OPERATIONS ====================

    /**
     * Requests a new loan
     * RULE: Client must have at least 2 active accounts
     */
    public Loan requestLoan(
            Long clientId,
            BigDecimal amount,
            Double interestRate,
            int termMonths,
            Long disbursementAccountId) {

        Client client = findClientOrThrow(clientId);
        validateMinimumAccounts(clientId);
        validateLoanAmount(amount);

        BankAccount disbursementAccount = validateDisbursementAccount(
            disbursementAccountId, clientId);

        Loan loan = new Loan();
        loan.setId(null);
        loan.setClient(client);
        loan.setRequestedAmount(amount);
        loan.setInterestRate(interestRate);
        loan.setTermMonths(termMonths);
        loan.setLoanStatus(LoanStatus.PENDING);
        loan.setDisbursementTargetAccount(disbursementAccount);

        Loan saved = loanPort.save(loan);
        auditService.logOperation("LOAN_REQUESTED", saved.getId() != null ? saved.getId().toString() : null);

        return saved;
    }

    // ==================== APPROVAL OPERATIONS ====================

    /**
     * Approves a loan request
     * Only ADMINISTRATORS can approve loans
     */
    public Loan approveLoan(Long loanId, String adminId) {
        Loan loan = findLoanOrThrow(loanId);

        validatePendingStatus(loan);

        loan.setLoanStatus(LoanStatus.APPROVED);
        loan.setApprovalDate(LocalDate.now());
        Loan updated = loanPort.save(loan);

        auditService.logOperation("LOAN_APPROVED", updated.getId() != null ? updated.getId().toString() : null);

        return updated;
    }

    /**
     * Rejects a loan request
     */
    public Loan rejectLoan(Long loanId, String reason) {
        Loan loan = findLoanOrThrow(loanId);

        validatePendingStatus(loan);

        loan.setLoanStatus(LoanStatus.REJECTED);
        Loan updated = loanPort.save(loan);

        auditService.logOperation("LOAN_REJECTED", updated.getId() != null ? updated.getId().toString() : null);

        return updated;
    }

    // ==================== DISBURSEMENT OPERATIONS ====================

    /**
     * Disburses an approved loan
     * Credits the loan amount to the disbursement account
     */
    public Loan disburseLoan(Long loanId) {
        Loan loan = findLoanOrThrow(loanId);

        validateApprovedStatus(loan);

        BankAccount account = bankAccountPort.findById(loan.getDisbursementTargetAccount().getId());
        if (account == null) {
            throw new BusinessException("Cuenta de desembolso no encontrada");
        }

        account.credit(loan.getRequestedAmount());
        bankAccountPort.save(account);

        loan.setLoanStatus(LoanStatus.DISBURSED);
        loan.setDisbursementDate(LocalDate.now());
        Loan updated = loanPort.save(loan);

        auditService.logOperation("LOAN_DISBURSED", updated.getId() != null ? updated.getId().toString() : null);

        return updated;
    }

    // ==================== QUERY OPERATIONS ====================

    public List<Loan> findClientLoans(Long clientId) {
        return loanPort.findByRequestingClientId(clientId);
    }

    public Loan findById(Long id) {
        return loanPort.findById(id);
    }

    public List<Loan> findAll() {
        return loanPort.findAll();
    }

    // ==================== VALIDATION METHODS ====================

    private Client findClientOrThrow(Long clientId) {
        Client client = clientPort.findById(clientId);
        if (client == null) {
            throw new UserNotFoundException("Cliente no encontrado: " + clientId);
        }
        return client;
    }

    private Loan findLoanOrThrow(Long loanId) {
        Loan loan = loanPort.findById(loanId);
        if (loan == null) {
            throw new BusinessException("Préstamo no encontrado: " + loanId);
        }
        return loan;
    }

    private void validateMinimumAccounts(Long clientId) {
        long activeAccounts = bankAccountPort.countByClientId(clientId);
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

    private BankAccount validateDisbursementAccount(Long accountId, Long clientId) {
        BankAccount account = bankAccountPort.findById(accountId);
        if (account == null) {
            throw new BusinessException("Cuenta de desembolso no encontrada");
        }

        if (account.getClient() == null || !account.getClient().getId().equals(clientId)) {
            throw new IllegalArgumentException(
                "La cuenta de desembolso debe pertenecer al cliente");
        }

        return account;
    }

    private void validatePendingStatus(Loan loan) {
        if (loan.getLoanStatus() != LoanStatus.PENDING) {
            throw new IllegalStateException(
                "Solo se pueden procesar préstamos en estado PENDING");
        }
    }

    private void validateApprovedStatus(Loan loan) {
        if (loan.getLoanStatus() != LoanStatus.APPROVED) {
            throw new IllegalStateException(
                "Solo se pueden desembolsar préstamos APROBADOS");
        }
    }
}
