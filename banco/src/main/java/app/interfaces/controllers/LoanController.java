package app.interfaces.controllers;

import app.application.usecases.LoanManagementUseCase;
import app.domain.models.Loan;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LoanController {

    private final LoanManagementUseCase loanManagementUseCase;

    @PostMapping
    public ResponseEntity<?> request(@RequestBody Loan loan) {
        try {
            Loan created = loanManagementUseCase.requestLoan(loan);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approve(
            @PathVariable Long id,
            @RequestParam Long userId) {
        try {
            Loan approved = loanManagementUseCase.approveLoan(id, userId);
            return ResponseEntity.ok(approved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/disburse")
    public ResponseEntity<?> disburse(@PathVariable Long id) {
        try {
            Loan disbursed = loanManagementUseCase.disburseLoan(id);
            return ResponseEntity.ok(disbursed);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Loan>> findAll() {
        return ResponseEntity.ok(loanManagementUseCase.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        Loan loan = loanManagementUseCase.findById(id);
        if (loan == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(loan);
    }
}
