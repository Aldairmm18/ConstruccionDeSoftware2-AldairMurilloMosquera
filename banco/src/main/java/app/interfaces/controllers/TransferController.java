package app.interfaces.controllers;

import app.application.usecases.TransferManagementUseCase;
import app.domain.models.BankAccount;
import app.domain.models.Transfer;
import app.interfaces.controllers.requests.TransferRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TransferController {

    private final TransferManagementUseCase transferManagementUseCase;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody TransferRequest request) {
        try {
            // El use case resuelve cuentas reales y valida reglas
            BankAccount source = new BankAccount();
            source.setAccountNumber(request.getSourceAccountNumber());

            BankAccount target = new BankAccount();
            target.setAccountNumber(request.getTargetAccountNumber());

            Transfer transfer = new Transfer();
            transfer.setSourceAccount(source);
            transfer.setTargetAccount(target);
            transfer.setAmount(request.getAmount());

            Transfer created = transferManagementUseCase.createTransfer(transfer);
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
            Transfer approved = transferManagementUseCase.approveTransfer(id, userId);
            return ResponseEntity.ok(approved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Transfer>> findAll() {
        return ResponseEntity.ok(transferManagementUseCase.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        Transfer t = transferManagementUseCase.getTransferById(id);
        if (t == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(t);
    }
}
