package app.application.usecases;

import app.domain.Exceptions.AccessDeniedException;
import app.domain.Exceptions.BusinessException;
import app.domain.models.Loan;
import app.domain.models.SystemRole;
import app.domain.models.User;
import app.domain.ports.UserPort;
import app.domain.services.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoanManagementUseCaseImpl implements LoanManagementUseCase {

    private final LoanService loanService;
    private final UserPort userPort;

    @Override
    @Transactional
    public Loan requestLoan(String clientId, BigDecimal amount, BigDecimal interestRate,
                            int termMonths, String disbursementAccountId) {
        return loanService.requestLoan(
                Long.parseLong(clientId), amount,
                interestRate != null ? interestRate.doubleValue() : null,
                termMonths, Long.parseLong(disbursementAccountId));
    }

    @Override
    @Transactional
    public Loan approveLoan(String loanId, String adminId) {
        requireRole(adminId, SystemRole.INTERNAL_ANALYST, "Only INTERNAL_ANALYST can approve loans");
        return loanService.approveLoan(Long.parseLong(loanId), adminId);
    }

    @Override
    @Transactional
    public Loan rejectLoan(String loanId, String adminId, String reason) {
        requireRole(adminId, SystemRole.INTERNAL_ANALYST, "Only INTERNAL_ANALYST can reject loans");
        return loanService.rejectLoan(Long.parseLong(loanId), reason);
    }

    @Override
    @Transactional
    public Loan disburseLoan(String loanId) {
        return loanService.disburseLoan(Long.parseLong(loanId));
    }

    @Override
    public List<Loan> findLoansByClient(String clientId) {
        return loanService.findClientLoans(Long.parseLong(clientId));
    }

    @Override
    public Optional<Loan> findById(String id) {
        return Optional.ofNullable(loanService.findById(Long.parseLong(id)));
    }

    @Override
    public List<Loan> findAll() {
        return loanService.findAll();
    }

    private void requireRole(String userId, SystemRole required, String message) {
        User user = userPort.findById(Long.parseLong(userId));
        if (user == null) throw new BusinessException("User not found");
        if (user.getSystemRole() != required) throw new AccessDeniedException(message);
    }
}
