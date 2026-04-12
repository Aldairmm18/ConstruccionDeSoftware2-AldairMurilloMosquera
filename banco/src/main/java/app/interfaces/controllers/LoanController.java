package app.interfaces.controllers;

import app.application.usecases.LoanManagementUseCase;
import app.domain.models.Loan;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LoanController {

    private final LoanManagementUseCase loanManagementUseCase;

    @PostMapping("/request")
    public ResponseEntity<?> request(@RequestBody Map<String, Object> payload) {
        try {
            String clientId = (String) payload.get("clientId");
            BigDecimal amount = new BigDecimal(payload.get("amount").toString());
            BigDecimal rate = new BigDecimal(payload.get("interestRate").toString());
            int term = Integer.parseInt(payload.get("termMonths").toString());
            String acc = (String) payload.get("disbursementAccountId");
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(loanManagementUseCase.requestLoan(clientId, amount, rate, term, acc));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable String id, @RequestParam String adminId) {
        try {
            return ResponseEntity.ok(loanManagementUseCase.approveLoan(id, adminId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/disburse")
    public ResponseEntity<?> disburse(@PathVariable String id) {
        try {
            return ResponseEntity.ok(loanManagementUseCase.disburseLoan(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Loan>> findByClient(@PathVariable String clientId) {
        return ResponseEntity.ok(loanManagementUseCase.findLoansByClient(clientId));
    }

    @GetMapping
    public ResponseEntity<List<Loan>> findAll() {
        return ResponseEntity.ok(loanManagementUseCase.findAll());
    }
}
