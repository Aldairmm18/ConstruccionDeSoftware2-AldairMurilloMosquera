package app.interfaces.controllers;

import app.domain.models.Loan;
import app.domain.services.PrestamoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prestamos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PrestamoController {
    
    private final PrestamoService prestamoService;
    
    @PostMapping
    public ResponseEntity<?> solicitar(@RequestBody Loan prestamo) {
        try {
            Loan creado = prestamoService.solicitar(prestamo);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/aprobar")
    public ResponseEntity<?> aprobar(
            @PathVariable Long id,
            @RequestParam Long usuarioId) {
        try {
            Loan aprobado = prestamoService.aprobar(id, usuarioId);
            return ResponseEntity.ok(aprobado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/desembolsar")
    public ResponseEntity<?> desembolsar(@PathVariable Long id) {
        try {
            Loan desembolsado = prestamoService.desembolsar(id);
            return ResponseEntity.ok(desembolsado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Loan>> listar() {
        return ResponseEntity.ok(prestamoService.listarTodos());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        Loan loan = prestamoService.buscarPorId(id);
        if (loan == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(loan);
    }
}
