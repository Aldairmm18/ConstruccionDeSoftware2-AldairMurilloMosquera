package app.domain.ports;

import app.domain.models.Client;
import java.util.List;

public interface ClientPort {

  Client save(Client client);

  Client findById(Long id);

  List<Client> findAll();

  boolean existsByDocument(String document);
  Client findByDocument(String document);
  boolean existsByEmail(String email);
}
