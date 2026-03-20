package app.domain.ports;

import app.domain.models.Transfer;
import java.util.List;

public interface TransferPort {

  Transfer save(Transfer transfer);

  Transfer findById(Long id);

  List<Transfer> findAll();
}
