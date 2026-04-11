package app.interfaces.controllers;

import app.domain.models.BankAccount;
import app.domain.models.Transfer;
import app.domain.ports.BankAccountPort;
import app.domain.services.TransferenciaService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transferencias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TransferenciaController {
    
    private final TransferenciaService transferenciaService;
    private final BankAccountPort bankAccountPort;
    
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody TransferenciaDTO dto) {
        try {
            BankAccount origen = bankAccountPort.findByAccountNumber(dto.getNumeroCuentaOrigen());
            if (origen == null) throw new RuntimeException("Cuenta origen no encontrada");
            
            BankAccount destino = bankAccountPort.findByAccountNumber(dto.getNumeroCuentaDestino());
            if (destino == null) throw new RuntimeException("Cuenta destino no encontrada");
            
            Transfer transferencia = new Transfer();
            transferencia.setSourceAccount(origen);
            transferencia.setTargetAccount(destino);
            transferencia.setAmount(dto.getMonto());
            
            Transfer creada = transferenciaService.crear(transferencia);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(creada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/aprobar")
    public ResponseEntity<?> aprobar(
            @PathVariable Long id,
            @RequestParam Long usuarioId) {
        try {
            Transfer aprobada = transferenciaService.aprobar(id, usuarioId);
            return ResponseEntity.ok(aprobada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Transfer>> listar() {
        return ResponseEntity.ok(transferenciaService.listarTodas());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        Transfer t = transferenciaService.buscarPorId(id);
        if (t == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(t);
    }
}

@Getter
@Setter
class TransferenciaDTO {
    private String numeroCuentaOrigen;
    private String numeroCuentaDestino;
    private java.math.BigDecimal monto;
}
