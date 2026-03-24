package app.application.adapters.persistence.sql;

import app.application.adapters.persistence.sql.entities.ClientEntity;
import app.application.adapters.persistence.sql.repositories.ClientRepository;
import app.domain.models.PersonClient;
import app.domain.ports.ClientPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientPersistenceAdapter implements ClientPort {

  private final ClientRepository clientRepository;

  @Override
  public PersonClient save(PersonClient client) {
    ClientEntity entity = toEntity(client);
    if (entity != null && (entity.getId() == null || entity.getId() == 0L)) {
      entity.setId(null);
    }
    ClientEntity saved = clientRepository.save(entity);
    return toModel(saved);
  }

  @Override
  public PersonClient findById(Long id) {
    return clientRepository.findById(id).map(this::toModel).orElse(null);
  }

  @Override
  public List<PersonClient> findAll() {
    return clientRepository.findAll().stream().map(this::toModel).toList();
  }

  @Override
  public boolean existsByDocument(String document) {
    return clientRepository.existsByDocument(document);
  }

  @Override
  public PersonClient findByDocument(String document) {
    return clientRepository.findByDocument(document).map(this::toModel).orElse(null);
  }

  @Override
  public boolean existsByEmail(String email) {
    return clientRepository.existsByEmail(email);
  }

  private ClientEntity toEntity(PersonClient model) {
    if (model == null) return null;
    ClientEntity entity = new ClientEntity();
    entity.setId(model.getId());
    entity.setName(model.getName());
    entity.setDocument(model.getDocument());
    entity.setEmail(model.getEmail());
    entity.setPhone(model.getPhone());
    entity.setAddress(model.getAddress());
    entity.setBirthDate(model.getBirthDate());
    return entity;
  }

  private PersonClient toModel(ClientEntity entity) {
    if (entity == null) return null;
    PersonClient model = new PersonClient();
    model.setId(entity.getId());
    model.setName(entity.getName());
    model.setDocument(entity.getDocument());
    model.setEmail(entity.getEmail());
    model.setPhone(entity.getPhone());
    model.setAddress(entity.getAddress());
    model.setBirthDate(entity.getBirthDate());
    return model;
  }
}
