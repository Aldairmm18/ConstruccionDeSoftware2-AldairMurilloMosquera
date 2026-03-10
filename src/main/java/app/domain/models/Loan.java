package app.domain.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
public class Loan {

    private long loanId;
    private LoanType loanType;
    private String requestingClientId;
    private double requestedAmount;
    private double approvedAmount;
    private double interestRate;
    private int termMonths;
    private LoanStatus loanStatus;
    private Date approvalDate;
    private Date disbursementDate;
    private String disbursementTargetAccount;
}
