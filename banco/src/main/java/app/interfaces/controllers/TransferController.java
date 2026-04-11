package app.interfaces.controllers;

import app.domain.models.BankAccount;
import app.domain.models.Transfer;
import app.domain.ports.BankAccountPort;
import app.domain.services.TransferService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
    
    private final TransferService transferService;
    private final BankAccountPort bankAccountPort;
    
    @PostMapping
    public ResponseEntity<?> create(@RequestBody TransferDTO dto) {
        try {
            BankAccount source = bankAccountPort.findByAccountNumber(dto.getSourceAccountNumber());
            if (source == null) throw new RuntimeException("Source account not found");
            
            BankAccount target = bankAccountPort.findByAccountNumber(dto.getTargetAccountNumber());
            if (target == null) throw new RuntimeException("Target account not found");
            
            Transfer transfer = new Transfer();
            transfer.setSourceAccount(source);
            transfer.setTargetAccount(target);
            transfer.setAmount(dto.getAmount());
            
            Transfer created = transferService.createTransfer(transfer);
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
            Transfer approved = transferService.approveTransfer(id, userId);
            return ResponseEntity.ok(approved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Transfer>> findAll() {
        return ResponseEntity.ok(transferService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        Transfer t = transferService.findById(id);
        if (t == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(t);
    }
}

@Getter
@Setter
class TransferDTO {
    private String sourceAccountNumber;
    private String targetAccountNumber;
    private java.math.BigDecimal amount;
}
