package app.domain.ports;

import app.domain.models.TransferStatus;
import app.domain.models.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, String> {
    List<Transfer> findByStatus(TransferStatus status);
}
