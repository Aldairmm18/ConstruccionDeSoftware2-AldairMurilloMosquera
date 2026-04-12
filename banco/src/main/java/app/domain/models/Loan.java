package app.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    private Long id;

    private Client client;

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
