package app.interfaces.controllers;

import app.application.usecases.ClientManagementUseCase;
import app.domain.models.Client;
import app.domain.models.CorporateClient;
import app.domain.models.PersonClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClientController {

    private final ClientManagementUseCase clientManagementUseCase;

    @PostMapping("/natural")
    public ResponseEntity<?> registerNatural(@RequestBody PersonClient client) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(clientManagementUseCase.registerNaturalPerson(client));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/corporate")
    public ResponseEntity<?> registerCorporate(@RequestBody CorporateClient client) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(clientManagementUseCase.registerCorporateCompany(client));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Client>> findAll() {
        return ResponseEntity.ok(clientManagementUseCase.findAll());
    }

    @GetMapping("/{doc}")
    public ResponseEntity<?> findByDoc(@PathVariable String doc) {
        return clientManagementUseCase.findByIdentification(doc)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
