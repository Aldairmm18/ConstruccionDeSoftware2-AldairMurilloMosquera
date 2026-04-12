package app.interfaces.controllers;

import app.application.usecases.AccountManagementUseCase;
import app.domain.models.BankAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BankAccountController {

    private final AccountManagementUseCase accountManagementUseCase;

    @PostMapping
    public ResponseEntity<BankAccount> create(@RequestBody BankAccount account) {
        return ResponseEntity.ok(accountManagementUseCase.createAccount(account));
    }

    @PostMapping("/{id}/block")
    public ResponseEntity<BankAccount> block(@PathVariable Long id) {
        // La orquestacion vive en el use case
        return ResponseEntity.ok(accountManagementUseCase.blockAccount(id));
    }

    @GetMapping
    public ResponseEntity<List<BankAccount>> findAll() {
        return ResponseEntity.ok(accountManagementUseCase.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        BankAccount account = accountManagementUseCase.findById(id);
        if (account == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(account);
    }
}
