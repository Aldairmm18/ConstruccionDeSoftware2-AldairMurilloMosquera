package app.infrastructure.persistence;

import app.domain.models.Client;
import app.domain.ports.ClientPort;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ClientRepository extends SimpleJpaRepository<ClientEntity, Long> implements ClientPort {

  public ClientRepository(EntityManager entityManager) {
    super(ClientEntity.class, entityManager);
  }

  @Override
  public Client save(Client client) {
    ClientEntity entity = ClientEntity.fromDomain(client);
    if (entity != null && (entity.getId() == null || entity.getId() == 0L)) {
      entity.setId(null);
    }
    ClientEntity saved = super.save(entity);
    return saved != null ? saved.toDomain() : null;
  }

  @Override
  public Client findById(Long id) {
    return super.findById(id).map(ClientEntity::toDomain).orElse(null);
  }

  @Override
  public List<Client> findAll() {
    return super.findAll().stream().map(ClientEntity::toDomain).toList();
  }
}
