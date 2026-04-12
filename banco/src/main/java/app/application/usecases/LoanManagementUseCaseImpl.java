package app.application.usecases;

import app.domain.Exceptions.BusinessException;
import app.domain.Exceptions.InvalidAmountException;
import app.domain.Exceptions.LoanRejectedException;
import app.domain.models.BankAccount;
import app.domain.models.Loan;
import app.domain.models.LoanStatus;
import app.domain.models.OperationsLog;
import app.domain.models.User;
import app.domain.ports.BankAccountPort;
import app.domain.ports.LoanPort;
import app.domain.ports.OperationsLogPort;
import app.domain.ports.UserPort;
import app.domain.services.LoanDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanManagementUseCaseImpl implements LoanManagementUseCase {

    private final LoanPort loanPort;
    private final BankAccountPort bankAccountPort;
    private final LoanDomainService loanDomainService;
    private final OperationsLogPort operationsLogPort;
    private final UserPort userPort;
    private final TransactionManagementUseCase transactionManagementUseCase;

    @Override
    @Transactional
    public Loan requestLoan(Loan loan) {
        if (loan.getClient() == null || loan.getClient().getId() == null) {
            throw new BusinessException("Datos del cliente incompletos");
        }

        long activeAccounts = bankAccountPort.countByClientId(loan.getClient().getId());
        if (activeAccounts < 2) {
            throw new LoanRejectedException(
                String.format("Debe tener al menos 2 cuentas activas para solicitar un prestamo. Tiene: %d", activeAccounts));
        }

        if (loan.getRequestedAmount() == null || loan.getRequestedAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("El monto del prestamo debe ser mayor a 0");
        }

        if (loan.getDisbursementTargetAccount() == null
            || loan.getDisbursementTargetAccount().getAccountNumber() == null) {
            throw new BusinessException("Cuenta de desembolso no encontrada");
        }

        BankAccount disbursementAccount = bankAccountPort.findByAccountNumber(
            loan.getDisbursementTargetAccount().getAccountNumber());
        if (disbursementAccount == null) {
            throw new BusinessException("Cuenta de desembolso no encontrada");
        }

        if (disbursementAccount.getClient() == null
            || disbursementAccount.getClient().getId() == null
            || !disbursementAccount.getClient().getId().equals(loan.getClient().getId())) {
            throw new BusinessException("La cuenta de desembolso debe pertenecer al cliente");
        }

        // Validaciones de dominio sobre el prestamo
        loanDomainService.validateLoanCreation(loan);
        loan.setLoanStatus(LoanStatus.UNDER_REVIEW);

        Loan saved = loanPort.save(loan);
        recordLog("PRESTAMO_SOLICITADO", disbursementAccount, saved, null);

        return saved;
    }

    @Override
    @Transactional
    public Loan approveLoan(Long loanId, Long userId) {
        Loan loan = loanPort.findById(loanId);
        if (loan == null) {
            throw new BusinessException("Prestamo no encontrado");
        }

        if (loan.getLoanStatus() != LoanStatus.UNDER_REVIEW) {
            throw new BusinessException("Solo se pueden aprobar prestamos EN REVISION");
        }

        loan.setLoanStatus(LoanStatus.APPROVED);
        Loan saved = loanPort.save(loan);

        User user = userPort.findById(userId);
        recordLog("PRESTAMO_APROBADO", loan.getDisbursementTargetAccount(), saved, user);

        return saved;
    }

    @Override
    @Transactional
    public Loan disburseLoan(Long loanId) {
        Loan loan = loanPort.findById(loanId);
        if (loan == null) {
            throw new BusinessException("Prestamo no encontrado");
        }

        if (loan.getLoanStatus() != LoanStatus.APPROVED) {
            throw new BusinessException("Solo se pueden desembolsar prestamos APROBADOS");
        }

        // Se reutiliza el use case de transacciones para el desembolso
        transactionManagementUseCase.deposit(
            loan.getDisbursementTargetAccount().getAccountNumber(),
            loan.getRequestedAmount()
        );

        loan.setLoanStatus(LoanStatus.DISBURSED);
        loan.setDisbursementDate(LocalDate.now());

        Loan updated = loanPort.save(loan);
        recordLog("PRESTAMO_DESEMBOLSADO", loan.getDisbursementTargetAccount(), updated, null);

        return updated;
    }

    @Override
    public List<Loan> findAll() {
        return loanPort.findAll();
    }

    @Override
    public Loan findById(Long id) {
        return loanPort.findById(id);
    }

    @Override
    public List<Loan> findByClientId(Long clientId) {
        // El puerto expone findByRequestingClientId
        return loanPort.findByRequestingClientId(clientId);
    }

    // Registro de bitacora con referencias a entidades reales
    private void recordLog(String operation, BankAccount account, Loan l, User user) {
        OperationsLog log = new OperationsLog();
        log.setLogId(UUID.randomUUID().toString());
        log.setOperationDateTime(LocalDateTime.now());
        log.setOperationType(operation);
        log.setAffectedProduct(account);
        log.setUser(user);

        Map<String, Object> details = new HashMap<>();
        details.put("loanId", l.getId());
        if (l.getClient() != null) {
            details.put("clientId", l.getClient().getId());
        }
        details.put("amount", l.getRequestedAmount());
        details.put("status", l.getLoanStatus().toString());
        log.setDetailData(details);

        operationsLogPort.save(log);
    }
}
