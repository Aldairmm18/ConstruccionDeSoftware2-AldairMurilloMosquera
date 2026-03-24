package app.domain.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

  private Long loanId;
  private Long requestingClientId;
  private LoanType loanType;
  private LoanStatus loanStatus;
  private BigDecimal requestedAmount;
  private BigDecimal approvedAmount;
  private Double interestRate;
  private int termMonths;
  private LocalDate approvalDate;
  private LocalDate disbursementDate;
  private BankAccount disbursementTargetAccount;
}
