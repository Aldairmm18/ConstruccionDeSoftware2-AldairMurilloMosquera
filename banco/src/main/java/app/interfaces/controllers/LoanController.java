package app.interfaces.controllers;

import app.application.services.LoanService;
import app.domain.models.Loan;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
public class LoanController {

  private final LoanService loanService;

  @PostMapping
  public Loan create(@RequestBody Loan loan) {
    return loanService.create(loan);
  }

  @GetMapping("/{id}")
  public Loan findById(@PathVariable Long id) {
    return loanService.findById(id);
  }

  @GetMapping
  public List<Loan> findAll() {
    return loanService.findAll();
  }
}
