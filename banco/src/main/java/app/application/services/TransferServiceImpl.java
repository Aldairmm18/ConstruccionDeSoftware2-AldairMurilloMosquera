package app.application.services;

import app.domain.models.Transfer;
import app.domain.ports.TransferPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

  private final TransferPort transferPort;

  @Override
  public Transfer create(Transfer transfer) {
    return transferPort.save(transfer);
  }

  @Override
  public Transfer findById(Long id) {
    return transferPort.findById(id);
  }

  @Override
  public List<Transfer> findAll() {
    return transferPort.findAll();
  }
}
