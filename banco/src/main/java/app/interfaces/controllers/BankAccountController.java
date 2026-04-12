package app.interfaces.controllers;

import app.application.usecases.AccountManagementUseCase;
import app.domain.models.BankAccount;
import app.domain.models.AccountType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BankAccountController {

    private final AccountManagementUseCase accountManagementUseCase;

    @PostMapping("/savings")
    public ResponseEntity<?> openSavings(@RequestBody Map<String, Object> payload) {
        try {
            String clientId = (String) payload.get("clientId");
            BigDecimal initialDeposit = new BigDecimal(payload.get("initialDeposit").toString());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(accountManagementUseCase.openSavingsAccount(clientId, initialDeposit));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/checking")
    public ResponseEntity<?> openChecking(@RequestBody Map<String, Object> payload) {
        try {
            String clientId = (String) payload.get("clientId");
            BigDecimal initialDeposit = new BigDecimal(payload.get("initialDeposit").toString());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(accountManagementUseCase.openCheckingAccount(clientId, initialDeposit));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{number}/balance")
    public ResponseEntity<?> getBalance(@PathVariable String number) {
        try {
            return ResponseEntity.ok(Map.of("balance", accountManagementUseCase.getBalance(number)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<BankAccount>> findAll() {
        return ResponseEntity.ok(accountManagementUseCase.findAll());
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<BankAccount>> findByClient(@PathVariable String clientId) {
        return ResponseEntity.ok(accountManagementUseCase.findByClientId(clientId));
    }
}
