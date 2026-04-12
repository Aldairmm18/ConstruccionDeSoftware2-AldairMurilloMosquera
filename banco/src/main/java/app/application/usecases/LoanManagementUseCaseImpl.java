package app.application.usecases;

import app.domain.Exceptions.*;
import app.domain.models.*;
import app.domain.ports.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class LoanManagementUseCaseImpl implements LoanManagementUseCase {

    private final LoanRepository loanRepository;
    private final ClientRepository clientRepository;
    private final BankAccountRepository bankAccountRepository;
    private final OperationsLogRepository operationsLogRepository;

    @Override
    @Transactional
    public Loan requestLoan(String clientId, BigDecimal amount, BigDecimal interestRate, int termMonths, String disbursementAccountId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new BusinessException("Client not found"));

        // RULE: At least 2 active accounts
        long activeAccounts = bankAccountRepository.countByClientId(clientId);
        if (activeAccounts < 2) {
            throw new LoanRejectedException("Client must have at least 2 active accounts to request a loan.");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Loan amount must be greater than 0");
        }

        BankAccount disbursementAccount = bankAccountRepository.findById(disbursementAccountId)
                .orElseThrow(() -> new BusinessException("Disbursement account not found"));

        if (!disbursementAccount.getClient().getId().equals(clientId)) {
            throw new BusinessException("Disbursement account must belong to the client");
        }

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
        registerLog("LOAN_REQUESTED", saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public Loan approveLoan(String loanId, String adminId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new BusinessException("Loan not found"));

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new BusinessException("Loan is not in PENDING state");
        }

        loan.setStatus(LoanStatus.APPROVED);
        Loan updated = loanRepository.save(loan);
        registerLog("LOAN_APPROVED", updated.getId());
        return updated;
    }

    @Override
    @Transactional
    public Loan rejectLoan(String loanId, String reason) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new BusinessException("Loan not found"));

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new BusinessException("Loan is not in PENDING state");
        }

        loan.setStatus(LoanStatus.REJECTED);
        Loan updated = loanRepository.save(loan);
        registerLog("LOAN_REJECTED", updated.getId());
        return updated;
    }

    @Override
    @Transactional
    public Loan disburseLoan(String loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new BusinessException("Loan not found"));

        if (loan.getStatus() != LoanStatus.APPROVED) {
            throw new BusinessException("Loan is not APPROVED for disbursement");
        }

        BankAccount account = loan.getDisbursementAccount();
        account.credit(loan.getAmount());
        bankAccountRepository.save(account);

        loan.setStatus(LoanStatus.DISBURSED);
        Loan updated = loanRepository.save(loan);
        registerLog("LOAN_DISBURSED", updated.getId());
        return updated;
    }

    @Override
    public List<Loan> findLoansByClient(String clientId) {
        return loanRepository.findByClientId(clientId);
    }

    @Override
    public Optional<Loan> findById(String id) {
        return loanRepository.findById(id);
    }

    @Override
    public List<Loan> findAll() {
        return loanRepository.findAll();
    }

    private void registerLog(String operation, String loanId) {
        OperationsLog log = new OperationsLog();
        log.setId(UUID.randomUUID().toString());
        log.setTimestamp(LocalDateTime.now());
        log.setOperation(operation);
        
        Map<String, String> details = new HashMap<>();
        details.put("loanId", loanId);
        log.setDetails(details);
        
        operationsLogRepository.save(log);
    }
}
