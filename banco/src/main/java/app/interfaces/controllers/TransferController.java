package app.interfaces.controllers;

import app.application.usecases.TransferManagementUseCase;
import app.domain.models.Transfer;
import app.domain.models.BankAccount;
import app.domain.ports.TransferPort;
import app.interfaces.controllers.requests.TransferRequest;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transfers")
@RequiredArgsConstructor
public class TransferController {

  private final TransferManagementUseCase transferManagementUseCase;
  private final TransferPort transferPort;

  @PostMapping
  public ResponseEntity<Transfer> create(@Valid @RequestBody TransferRequest request) {
    Transfer transfer = toModel(request);
    Transfer createdTransfer = transferManagementUseCase.createTransfer(transfer);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdTransfer);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Transfer> findById(@PathVariable Long id) {
    Transfer transfer = transferPort.findById(id);
    if (transfer == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(transfer);
  }

  @GetMapping
  public ResponseEntity<List<Transfer>> findAll() {
    return ResponseEntity.ok(transferPort.findAll());
  }

  private Transfer toModel(TransferRequest request) {
    Transfer transfer = new Transfer();
    transfer.setAmount(request.getAmount());
    transfer.setCreationDate(LocalDateTime.now());

    if (request.getSourceAccount() != null) {
        BankAccount source = new BankAccount();
        source.setAccountNumber(request.getSourceAccount());
        transfer.setSourceAccount(source);
    }
    if (request.getTargetAccount() != null) {
        BankAccount target = new BankAccount();
        target.setAccountNumber(request.getTargetAccount());
        transfer.setTargetAccount(target);
    }
    return transfer;
  }
}
