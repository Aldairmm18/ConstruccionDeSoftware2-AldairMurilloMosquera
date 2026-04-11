package app.interfaces.controllers;

import app.domain.services.TransaccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/transacciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TransaccionController {
    
    private final TransaccionService transaccionService;
    
    @PostMapping("/deposito")
    public ResponseEntity<?> depositar(@RequestParam String accountNumber, @RequestParam BigDecimal amount) {
        try {
            transaccionService.depositar(accountNumber, amount);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/retiro")
    public ResponseEntity<?> retirar(@RequestParam String accountNumber, @RequestParam BigDecimal amount) {
        try {
            transaccionService.retirar(accountNumber, amount);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
