package app.domain.ports;

import app.domain.models.PersonClient;
import java.util.List;

public interface ClientPort {

  PersonClient save(PersonClient client);

  PersonClient findById(Long id);

  List<PersonClient> findAll();

  boolean existsByDocument(String document);
  PersonClient findByDocument(String document);
  boolean existsByEmail(String email);
}
