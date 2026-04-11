package app.interfaces.controllers;

import app.domain.models.BankAccount;
import app.domain.ports.BankAccountPort;
import app.application.usecases.AccountManagementUseCase;
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
    private final BankAccountPort bankAccountPort;
    
    @PostMapping
    public ResponseEntity<BankAccount> create(@RequestBody BankAccount account) {
        return ResponseEntity.ok(accountManagementUseCase.createAccount(account));
    }
    
    @GetMapping
    public ResponseEntity<List<BankAccount>> findAll() {
        return ResponseEntity.ok(bankAccountPort.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        BankAccount account = bankAccountPort.findById(id);
        if (account == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(account);
    }
}
