package app.domain.ports;

import app.domain.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
    boolean existsByIdentification(String identification);
    boolean existsByEmail(String email);
    Optional<Client> findByIdentification(String identification);
    Optional<Client> findByEmail(String email);
}
