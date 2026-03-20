package app.application.services;

import app.domain.models.Client;
import app.domain.ports.ClientPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

  private final ClientPort clientPort;

  @Override
  public Client create(Client client) {
    return clientPort.save(client);
  }

  @Override
  public Client findById(Long id) {
    return clientPort.findById(id);
  }

  @Override
  public List<Client> findAll() {
    return clientPort.findAll();
  }
}
