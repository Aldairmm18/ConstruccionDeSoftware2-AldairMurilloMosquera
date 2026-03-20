package app.application.services;

import app.domain.models.Client;
import java.util.List;

public interface ClientService {

  Client create(Client client);

  Client findById(Long id);

  List<Client> findAll();
}
