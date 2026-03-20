package app.application.adapters.persistence.sql.repositories;

import app.application.adapters.persistence.sql.entities.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Long> {
  boolean existsByDocument(String document);
  Optional<ClientEntity> findByDocument(String document);
  boolean existsByEmail(String email);
}
