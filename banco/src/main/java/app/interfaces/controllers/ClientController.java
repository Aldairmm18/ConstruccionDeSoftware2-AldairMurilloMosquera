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
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

  private final ClientManagementUseCase clientManagementUseCase;
  private final ClientPort clientPort;

  @PostMapping
  public ResponseEntity<PersonClient> create(@Valid @RequestBody ClientRequest request) {
    PersonClient client = toModel(request);
    PersonClient createdClient = clientManagementUseCase.createNaturalClient(client);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdClient);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PersonClient> findById(@PathVariable Long id) {
    PersonClient client = clientPort.findById(id);
    if (client == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(client);
  }

  @GetMapping
  public ResponseEntity<List<PersonClient>> findAll() {
    return ResponseEntity.ok(clientPort.findAll());
  }

  private PersonClient toModel(ClientRequest request) {
    PersonClient client = new PersonClient();
    client.setName(request.getName());
    client.setDocument(request.getDocument());
    client.setEmail(request.getEmail());
    client.setPhone(request.getPhone());
    client.setAddress(request.getAddress());
    client.setBirthDate(request.getBirthDate());
    return client;
  }
}
