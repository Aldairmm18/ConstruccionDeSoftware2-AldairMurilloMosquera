package app.application.usecases;

import app.domain.Exceptions.*;
import app.domain.models.*;
import app.domain.ports.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

// TODO: Delegate business logic to domain services instead of implementing directly here

@Service
@RequiredArgsConstructor
public class LoanManagementUseCaseImpl implements LoanManagementUseCase {

    private final LoanPort loanPort;
    private final ClientPort clientPort;
    private final BankAccountPort bankAccountPort;
    private final OperationsLogPort operationsLogPort;
    private final UserPort userPort;

    @Override
    @Transactional
    public Loan requestLoan(String clientId, BigDecimal amount, BigDecimal interestRate, int termMonths, String disbursementAccountId) {
        Client client = clientPort.findById(Long.parseLong(clientId));
        if (client == null) {
            throw new BusinessException("Client not found");
        }

        // RULE: At least 2 active accounts
        long activeAccounts = bankAccountPort.countByClientId(Long.parseLong(clientId));
        if (activeAccounts < 2) {
            throw new LoanRejectedException("Client must have at least 2 active accounts to request a loan.");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Loan amount must be greater than 0");
        }

        BankAccount disbursementAccount = bankAccountPort.findById(Long.parseLong(disbursementAccountId));
        if (disbursementAccount == null) {
            throw new BusinessException("Disbursement account not found");
        }

        if (disbursementAccount.getClient() == null
                || !disbursementAccount.getClient().getId().equals(Long.parseLong(clientId))) {
            throw new BusinessException("Disbursement account must belong to the client");
        }

        Loan loan = new Loan();
        loan.setId(null);
        loan.setClient(client);
        loan.setRequestedAmount(amount);
        loan.setInterestRate(interestRate != null ? interestRate.doubleValue() : null);
        loan.setTermMonths(termMonths);
        loan.setLoanStatus(LoanStatus.UNDER_REVIEW);
        loan.setDisbursementTargetAccount(disbursementAccount);

        Loan saved = loanPort.save(loan);
        registerLog("LOAN_REQUESTED", saved.getId() != null ? saved.getId().toString() : null);
        return saved;
    }

    @Override
    @Transactional
    public Loan approveLoan(String loanId, String adminId) {
        User admin = userPort.findById(Long.parseLong(adminId));
        if (admin == null) {
            throw new BusinessException("User not found");
        }
        if (admin.getSystemRole() != SystemRole.INTERNAL_ANALYST) {
            throw new AccessDeniedException("Only INTERNAL_ANALYST can approve/reject loans");
        }

        Loan loan = loanPort.findById(Long.parseLong(loanId));
        if (loan == null) {
            throw new BusinessException("Loan not found");
        }

        if (loan.getLoanStatus() != LoanStatus.UNDER_REVIEW) {
            throw new BusinessException("Loan is not in UNDER_REVIEW state");
        }

        loan.setLoanStatus(LoanStatus.APPROVED);
        loan.setApprovalDate(LocalDate.now());
        Loan updated = loanPort.save(loan);
        registerLog("LOAN_APPROVED", updated.getId() != null ? updated.getId().toString() : null,
                admin.getId(), admin.getUsername());
        return updated;
    }

    @Override
    @Transactional
    public Loan rejectLoan(String loanId, String adminId, String reason) {
        User admin = userPort.findById(Long.parseLong(adminId));
        if (admin == null) {
            throw new BusinessException("User not found");
        }
        if (admin.getSystemRole() != SystemRole.INTERNAL_ANALYST) {
            throw new AccessDeniedException("Only INTERNAL_ANALYST can approve/reject loans");
        }

        Loan loan = loanPort.findById(Long.parseLong(loanId));
        if (loan == null) {
            throw new BusinessException("Loan not found");
        }

        if (loan.getLoanStatus() != LoanStatus.UNDER_REVIEW) {
            throw new BusinessException("Loan is not in UNDER_REVIEW state");
        }

        loan.setLoanStatus(LoanStatus.REJECTED);
        Loan updated = loanPort.save(loan);
        registerLog("LOAN_REJECTED", updated.getId() != null ? updated.getId().toString() : null,
                admin.getId(), admin.getUsername());
        return updated;
    }

    @Override
    @Transactional
    public Loan disburseLoan(String loanId) {
        Loan loan = loanPort.findById(Long.parseLong(loanId));
        if (loan == null) {
            throw new BusinessException("Loan not found");
        }

        if (loan.getLoanStatus() != LoanStatus.APPROVED) {
            throw new BusinessException("Loan is not APPROVED for disbursement");
        }

        BankAccount account = loan.getDisbursementTargetAccount();
        account.credit(loan.getRequestedAmount());
        bankAccountPort.save(account);

        loan.setLoanStatus(LoanStatus.DISBURSED);
        loan.setDisbursementDate(LocalDate.now());
        Loan updated = loanPort.save(loan);
        registerLog("LOAN_DISBURSED", updated.getId() != null ? updated.getId().toString() : null);
        return updated;
    }

    @Override
    public List<Loan> findLoansByClient(String clientId) {
        return loanPort.findByRequestingClientId(Long.parseLong(clientId));
    }

    @Override
    public Optional<Loan> findById(String id) {
        return Optional.ofNullable(loanPort.findById(Long.parseLong(id)));
    }

    @Override
    public List<Loan> findAll() {
        return loanPort.findAll();
    }

    private void registerLog(String operation, String loanId) {
        registerLog(operation, loanId, null, null);
    }

    private void registerLog(String operation, String loanId, Long userId, String username) {
        OperationsLog log = new OperationsLog();
        log.setLogId(UUID.randomUUID().toString());
        log.setOperationType(operation);
        log.setOperationDateTime(LocalDateTime.now());

        Map<String, Object> details = new HashMap<>();
        details.put("loanId", loanId);
        if (userId != null) {
            details.put("performedByUserId", userId);
        }
        if (username != null) {
            details.put("performedByUsername", username);
        }
        log.setDetailData(details);

        operationsLogPort.save(log);
    }
}
