package app.application.adapters.persistence.sql;

import app.application.adapters.persistence.sql.entities.TransferEntity;
import app.application.adapters.persistence.sql.repositories.TransferRepository;
import app.domain.models.Transfer;
import app.domain.ports.TransferPort;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferPersistenceAdapter implements TransferPort {

  private final TransferRepository transferRepository;

  @Override
  public Transfer save(Transfer transfer) {
    TransferEntity entity = TransferEntity.fromDomain(transfer);
    // Ensure ID is null if creating a new one (legacy fix)
    if (entity != null && (entity.getTransferId() == null || entity.getTransferId() == 0L)) {
      entity.setTransferId(null);
    }
    TransferEntity saved = transferRepository.save(entity);
    return saved.toDomain();
  }

  @Override
  public Transfer findById(Long id) {
    return transferRepository.findById(id).map(TransferEntity::toDomain).orElse(null);
  }

  @Override
  public List<Transfer> findAll() {
    return transferRepository.findAll().stream().map(TransferEntity::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Transfer> findBySourceAccount(String sourceAccount) {
    return transferRepository.findBySourceAccount(sourceAccount).stream().map(TransferEntity::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Transfer> findByTargetAccount(String targetAccount) {
    return transferRepository.findByTargetAccount(targetAccount).stream().map(TransferEntity::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Transfer> findByTransferStatus(app.domain.models.TransferStatus transferStatus) {
    return transferRepository.findByTransferStatus(transferStatus).stream().map(TransferEntity::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Transfer> findByTransferStatusAndCreationDateBefore(app.domain.models.TransferStatus status, java.time.LocalDateTime dateTime) {
    return transferRepository.findByTransferStatusAndCreationDateBefore(status, dateTime).stream().map(TransferEntity::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Transfer> findPendingApprovalOlderThanMinutes(int minutes) {
    java.time.LocalDateTime threshold = java.time.LocalDateTime.now().minusMinutes(minutes);
    return transferRepository.findByTransferStatusAndCreationDateBefore(
        app.domain.models.TransferStatus.AWAITING_APPROVAL, threshold)
        .stream().map(TransferEntity::toDomain).collect(Collectors.toList());
  }
}
