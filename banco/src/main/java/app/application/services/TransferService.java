package app.application.services;

import app.domain.models.Transfer;
import java.util.List;

public interface TransferService {

  Transfer create(Transfer transfer);

  Transfer findById(Long id);

  List<Transfer> findAll();
}
