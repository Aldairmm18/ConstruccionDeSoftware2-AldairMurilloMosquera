package app.domain.ports;

import app.domain.models.Transfer;
import java.util.List;

public interface TransferPort {

  Transfer save(Transfer transfer);

  Transfer findById(Long id);

  List<Transfer> findAll();

  List<Transfer> findBySourceAccount(String sourceAccount);
  List<Transfer> findByTargetAccount(String targetAccount);
  List<Transfer> findByTransferStatus(app.domain.models.TransferStatus transferStatus);
}
