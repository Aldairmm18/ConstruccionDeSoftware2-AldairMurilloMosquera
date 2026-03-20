package app.infrastructure.persistence;

import app.domain.models.Loan;
import app.domain.models.LoanStatus;
import app.domain.models.LoanType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "loan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long loanId;

  private Long requestingClientId;

  @Enumerated(EnumType.STRING)
  private LoanType loanType;

  @Enumerated(EnumType.STRING)
  private LoanStatus loanStatus;

  private Double requestedAmount;
  private Double approvedAmount;
  private Double interestRate;
  private int termMonths;
  private LocalDate approvalDate;
  private LocalDate disbursementDate;

  @ManyToOne
  @JoinColumn(name = "disbursement_target_account_id")
  private BankAccountEntity disbursementTargetAccount;

  public static LoanEntity fromDomain(Loan loan) {
    if (loan == null) {
      return null;
    }
    LoanEntity entity = new LoanEntity();
    entity.setLoanId(loan.getLoanId());
    entity.setRequestingClientId(loan.getRequestingClientId());
    entity.setLoanType(loan.getLoanType());
    entity.setLoanStatus(loan.getLoanStatus());
    entity.setRequestedAmount(loan.getRequestedAmount());
    entity.setApprovedAmount(loan.getApprovedAmount());
    entity.setInterestRate(loan.getInterestRate());
    entity.setTermMonths(loan.getTermMonths());
    entity.setApprovalDate(loan.getApprovalDate());
    entity.setDisbursementDate(loan.getDisbursementDate());
    entity.setDisbursementTargetAccount(
        BankAccountEntity.fromDomain(loan.getDisbursementTargetAccount()));
    return entity;
  }

  public Loan toDomain() {
    Loan loan = new Loan();
    loan.setLoanId(getLoanId());
    loan.setRequestingClientId(getRequestingClientId());
    loan.setLoanType(getLoanType());
    loan.setLoanStatus(getLoanStatus());
    loan.setRequestedAmount(getRequestedAmount());
    loan.setApprovedAmount(getApprovedAmount());
    loan.setInterestRate(getInterestRate());
    loan.setTermMonths(getTermMonths());
    loan.setApprovalDate(getApprovalDate());
    loan.setDisbursementDate(getDisbursementDate());
    loan.setDisbursementTargetAccount(
        getDisbursementTargetAccount() != null ? getDisbursementTargetAccount().toDomain() : null);
    return loan;
  }
}
