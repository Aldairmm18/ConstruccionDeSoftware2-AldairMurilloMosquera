package app.interfaces.controllers;

import app.application.usecases.ClientManagementUseCase;
import app.domain.models.PersonClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClientController {

  private final ClientManagementUseCase clientManagementUseCase;

  @PostMapping
  public ResponseEntity<PersonClient> create(@RequestBody PersonClient client) {
    PersonClient createdClient = clientManagementUseCase.createNaturalClient(client);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdClient);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PersonClient> findById(@PathVariable Long id) {
    PersonClient client = clientManagementUseCase.findById(id);
    if (client == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(client);
  }

  @GetMapping
  public ResponseEntity<List<PersonClient>> findAll() {
    return ResponseEntity.ok(clientManagementUseCase.findAll());
  }
}
