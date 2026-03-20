package app.interfaces.controllers;

import app.application.usecases.LoanManagementUseCase;
import app.domain.models.Loan;
import app.domain.models.BankAccount;
import app.domain.ports.LoanPort;
import app.interfaces.controllers.requests.LoanRequest;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
public class LoanController {

  private final LoanManagementUseCase loanManagementUseCase;
  private final LoanPort loanPort;

  @PostMapping
  public ResponseEntity<Loan> create(@Valid @RequestBody LoanRequest request) {
    Loan loan = toModel(request);
    Loan createdLoan = loanManagementUseCase.requestLoan(loan);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdLoan);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Loan> findById(@PathVariable Long id) {
    Loan loan = loanPort.findById(id);
    if (loan == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(loan);
  }

  @GetMapping
  public ResponseEntity<List<Loan>> findAll() {
    return ResponseEntity.ok(loanPort.findAll());
  }

  private Loan toModel(LoanRequest request) {
    Loan loan = new Loan();
    loan.setRequestingClientId(request.getRequestingClientId());
    loan.setRequestedAmount(request.getRequestedAmount());
    loan.setTermMonths(request.getTermMonths());
    if (request.getLoanType() != null) {
        loan.setLoanType(app.domain.models.LoanType.valueOf(request.getLoanType()));
    }

    if (request.getDisbursementTargetAccountId() != null) {
        BankAccount target = new BankAccount();
        target.setId(request.getDisbursementTargetAccountId());
        loan.setDisbursementTargetAccount(target);
    }
    return loan;
  }
}
