package app.application.adapters.persistence.sql;

import app.application.adapters.persistence.sql.entities.LoanEntity;
import app.application.adapters.persistence.sql.entities.BankAccountEntity;
import app.application.adapters.persistence.sql.repositories.LoanRepository;
import app.domain.models.Loan;
import app.domain.models.BankAccount;
import app.domain.ports.LoanPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanPersistenceAdapter implements LoanPort {

  private final LoanRepository loanRepository;

  @Override
  public Loan save(Loan loan) {
    LoanEntity entity = toEntity(loan);
    if (entity != null && (entity.getLoanId() == null || entity.getLoanId() == 0L)) {
      entity.setLoanId(null);
    }
    LoanEntity saved = loanRepository.save(entity);
    return toModel(saved);
  }

  @Override
  public Loan findById(Long id) {
    return loanRepository.findById(id).map(this::toModel).orElse(null);
  }

  @Override
  public List<Loan> findAll() {
    return loanRepository.findAll().stream().map(this::toModel).toList();
  }

  @Override
  public List<Loan> findByRequestingClientId(Long requestingClientId) {
    return loanRepository.findByRequestingClientId(requestingClientId).stream().map(this::toModel).toList();
  }

  @Override
  public List<Loan> findByLoanStatus(app.domain.models.LoanStatus loanStatus) {
    return loanRepository.findByLoanStatus(loanStatus).stream().map(this::toModel).toList();
  }

  private LoanEntity toEntity(Loan model) {
    if (model == null) return null;
    LoanEntity entity = new LoanEntity();
    entity.setLoanId(model.getLoanId());
    entity.setRequestingClientId(model.getRequestingClientId());
    entity.setLoanType(model.getLoanType());
    entity.setLoanStatus(model.getLoanStatus());
    entity.setRequestedAmount(model.getRequestedAmount());
    entity.setApprovedAmount(model.getApprovedAmount());
    entity.setInterestRate(model.getInterestRate());
    entity.setTermMonths(model.getTermMonths());
    entity.setApprovalDate(model.getApprovalDate());
    entity.setDisbursementDate(model.getDisbursementDate());
    if (model.getDisbursementTargetAccount() != null) {
       BankAccountEntity acc = new BankAccountEntity();
       acc.setId(model.getDisbursementTargetAccount().getId());
       acc.setAccountNumber(model.getDisbursementTargetAccount().getAccountNumber());
       entity.setDisbursementTargetAccount(acc);
    }
    return entity;
  }

  private Loan toModel(LoanEntity entity) {
    if (entity == null) return null;
    Loan model = new Loan();
    model.setLoanId(entity.getLoanId());
    model.setRequestingClientId(entity.getRequestingClientId());
    model.setLoanType(entity.getLoanType());
    model.setLoanStatus(entity.getLoanStatus());
    model.setRequestedAmount(entity.getRequestedAmount());
    model.setApprovedAmount(entity.getApprovedAmount());
    model.setInterestRate(entity.getInterestRate());
    model.setTermMonths(entity.getTermMonths());
    model.setApprovalDate(entity.getApprovalDate());
    model.setDisbursementDate(entity.getDisbursementDate());
    if (entity.getDisbursementTargetAccount() != null) {
       BankAccount acc = new BankAccount();
       acc.setId(entity.getDisbursementTargetAccount().getId());
       acc.setAccountNumber(entity.getDisbursementTargetAccount().getAccountNumber());
       model.setDisbursementTargetAccount(acc);
    }
    return model;
  }
}
