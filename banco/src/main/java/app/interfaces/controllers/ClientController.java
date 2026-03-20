package app.interfaces.controllers;

import app.application.services.ClientService;
import app.domain.models.Client;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

  private final ClientService clientService;

  @PostMapping
  public Client create(@RequestBody Client client) {
    return clientService.create(client);
  }

  @GetMapping("/{id}")
  public Client findById(@PathVariable Long id) {
    return clientService.findById(id);
  }

  @GetMapping
  public List<Client> findAll() {
    return clientService.findAll();
  }
}
