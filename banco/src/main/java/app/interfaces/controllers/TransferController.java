package app.interfaces.controllers;

import app.application.usecases.TransferManagementUseCase;
import app.domain.models.Transfer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TransferController {

    private final TransferManagementUseCase transferManagementUseCase;

    @PostMapping("/request")
    public ResponseEntity<?> request(@RequestBody Map<String, Object> payload) {
        try {
            String source = (String) payload.get("sourceAccountNumber");
            String target = (String) payload.get("targetAccountNumber");
            BigDecimal amount = new BigDecimal(payload.get("amount").toString());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(transferManagementUseCase.requestTransfer(source, target, amount));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable String id, @RequestParam String auditorId) {
        try {
            return ResponseEntity.ok(transferManagementUseCase.approveTransfer(id, auditorId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable String id,
                                     @RequestParam String auditorId,
                                     @RequestParam String reason) {
        try {
            return ResponseEntity.ok(transferManagementUseCase.rejectTransfer(id, auditorId, reason));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Transfer>> findPending() {
        return ResponseEntity.ok(transferManagementUseCase.findPendingTransfers());
    }

    @GetMapping
    public ResponseEntity<List<Transfer>> findAll() {
        return ResponseEntity.ok(transferManagementUseCase.findAll());
    }
}
