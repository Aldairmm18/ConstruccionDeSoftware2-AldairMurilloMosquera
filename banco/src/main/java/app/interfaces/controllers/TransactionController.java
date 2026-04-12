package app.interfaces.controllers;

import app.application.usecases.TransactionManagementUseCase;
import app.domain.models.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionManagementUseCase transactionManagementUseCase;

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody Map<String, Object> payload) {
        try {
            String number = (String) payload.get("accountNumber");
            BigDecimal amount = new BigDecimal(payload.get("amount").toString());
            String desc = (String) payload.get("description");
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(transactionManagementUseCase.makeDeposit(number, amount, desc));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/withdrawal")
    public ResponseEntity<?> withdrawal(@RequestBody Map<String, Object> payload) {
        try {
            String number = (String) payload.get("accountNumber");
            BigDecimal amount = new BigDecimal(payload.get("amount").toString());
            String desc = (String) payload.get("description");
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(transactionManagementUseCase.makeWithdrawal(number, amount, desc));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/service-payment")
    public ResponseEntity<?> payService(@RequestBody Map<String, Object> payload) {
        try {
            String number = (String) payload.get("accountNumber");
            String service = (String) payload.get("serviceName");
            String ref = (String) payload.get("reference");
            BigDecimal amount = new BigDecimal(payload.get("amount").toString());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(transactionManagementUseCase.payService(number, service, ref, amount));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/account/{number}")
    public ResponseEntity<List<Transaction>> getByAccount(@PathVariable String number) {
        return ResponseEntity.ok(transactionManagementUseCase.getTransactionsByAccount(number));
    }
}
