package app.interfaces.controllers;

import app.application.usecases.ClientManagementUseCase;
import app.domain.models.PersonClient;
import app.domain.ports.ClientPort;
import app.interfaces.controllers.requests.ClientRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClienteController {

  private final ClientManagementUseCase clientManagementUseCase;
  private final ClientPort clientPort;

  @PostMapping
  public ResponseEntity<PersonClient> crear(@Valid @RequestBody PersonClient client) {
    PersonClient createdClient = clientManagementUseCase.createNaturalClient(client);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdClient);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PersonClient> buscarPorId(@PathVariable Long id) {
    PersonClient client = clientPort.findById(id);
    if (client == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(client);
  }

  @GetMapping
  public ResponseEntity<List<PersonClient>> listar() {
    return ResponseEntity.ok(clientPort.findAll());
  }
}
