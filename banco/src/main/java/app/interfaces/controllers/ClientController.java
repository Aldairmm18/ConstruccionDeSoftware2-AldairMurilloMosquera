package app.interfaces.controllers;

import app.application.usecases.ClientManagementUseCase;
import app.domain.models.Client;
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
  public ResponseEntity<Client> create(@Valid @RequestBody ClientRequest request) {
    Client client = toModel(request);
    Client createdClient = clientManagementUseCase.createNaturalClient(client);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdClient);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Client> findById(@PathVariable Long id) {
    Client client = clientPort.findById(id);
    if (client == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(client);
  }

  @GetMapping
  public ResponseEntity<List<Client>> findAll() {
    return ResponseEntity.ok(clientPort.findAll());
  }

  private Client toModel(ClientRequest request) {
    Client client = new Client();
    client.setName(request.getName());
    client.setDocument(request.getDocument());
    client.setEmail(request.getEmail());
    client.setPhone(request.getPhone());
    client.setAddress(request.getAddress());
    client.setBirthDate(request.getBirthDate());
    return client;
  }
}
