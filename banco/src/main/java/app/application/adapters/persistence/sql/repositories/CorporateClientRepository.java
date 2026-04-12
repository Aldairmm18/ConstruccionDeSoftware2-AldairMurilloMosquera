package app.application.adapters.persistence.sql.repositories;

import app.application.adapters.persistence.sql.entities.CorporateClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CorporateClientRepository extends JpaRepository<CorporateClientEntity, Long> {
    boolean existsByDocument(String document);
    Optional<CorporateClientEntity> findByDocument(String document);
    boolean existsByEmail(String email);
}
