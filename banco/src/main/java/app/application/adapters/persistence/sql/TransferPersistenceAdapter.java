package app.application.adapters.persistence.sql;

import app.application.adapters.persistence.sql.entities.TransferEntity;
import app.application.adapters.persistence.sql.entities.BankAccountEntity;
import app.application.adapters.persistence.sql.entities.ClientEntity;
import app.application.adapters.persistence.sql.repositories.TransferRepository;
import app.domain.models.Transfer;
import app.domain.models.BankAccount;
import app.domain.models.PersonClient;
import app.domain.ports.TransferPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferPersistenceAdapter implements TransferPort {

  private final TransferRepository transferRepository;

  @Override
  public Transfer save(Transfer transfer) {
    TransferEntity entity = toEntity(transfer);
    if (entity != null && (entity.getTransferId() == null || entity.getTransferId() == 0L)) {
      entity.setTransferId(null);
    }
    TransferEntity saved = transferRepository.save(entity);
    return toModel(saved);
  }

  @Override
  public Transfer findById(Long id) {
    return transferRepository.findById(id).map(this::toModel).orElse(null);
  }

  @Override
  public List<Transfer> findAll() {
    return transferRepository.findAll().stream().map(this::toModel).toList();
  }

  @Override
  public List<Transfer> findBySourceAccount(String sourceAccount) {
    return transferRepository.findBySourceAccount(sourceAccount).stream().map(this::toModel).toList();
  }

  @Override
  public List<Transfer> findByTargetAccount(String targetAccount) {
    return transferRepository.findByTargetAccount(targetAccount).stream().map(this::toModel).toList();
  }

  @Override
  public List<Transfer> findByTransferStatus(app.domain.models.TransferStatus transferStatus) {
    return transferRepository.findByTransferStatus(transferStatus).stream().map(this::toModel).toList();
  }

  @Override
  public List<Transfer> findByTransferStatusAndCreationDateBefore(app.domain.models.TransferStatus status, java.time.LocalDateTime dateTime) {
    return transferRepository.findByTransferStatusAndCreationDateBefore(status, dateTime).stream().map(this::toModel).toList();
  }

  private TransferEntity toEntity(Transfer model) {
    if (model == null) return null;
    TransferEntity entity = new TransferEntity();
    entity.setTransferId(model.getTransferId());
    entity.setSourceAccount(toAccountEntity(model.getSourceAccount()));
    entity.setTargetAccount(toAccountEntity(model.getTargetAccount()));
    entity.setAmount(model.getAmount());
    entity.setTransferStatus(model.getTransferStatus());
    entity.setCreationDate(model.getCreationDate());
    entity.setApprovalDate(model.getApprovalDate());
    entity.setCreatorUserId(model.getCreatorUserId());
    entity.setApproverUserId(model.getApproverUserId());
    return entity;
  }

  private Transfer toModel(TransferEntity entity) {
    if (entity == null) return null;
    Transfer model = new Transfer();
    model.setTransferId(entity.getTransferId());
    model.setSourceAccount(toAccountModel(entity.getSourceAccount()));
    model.setTargetAccount(toAccountModel(entity.getTargetAccount()));
    model.setAmount(entity.getAmount());
    model.setTransferStatus(entity.getTransferStatus());
    model.setCreationDate(entity.getCreationDate());
    model.setApprovalDate(entity.getApprovalDate());
    model.setCreatorUserId(entity.getCreatorUserId());
    model.setApproverUserId(entity.getApproverUserId());
    return model;
  }

  private BankAccountEntity toAccountEntity(BankAccount model) {
    if (model == null) return null;
    BankAccountEntity entity = new BankAccountEntity();
    entity.setId(model.getId());
    entity.setAccountNumber(model.getAccountNumber());
    entity.setAccountType(model.getAccountType());
    entity.setAccountStatus(model.getAccountStatus());
    entity.setCurrency(model.getCurrency());
    entity.setCurrentBalance(model.getCurrentBalance());
    entity.setOpeningDate(model.getOpeningDate());
    if (model.getClient() != null) {
        ClientEntity ce = new ClientEntity();
        ce.setId(model.getClient().getId());
        ce.setName(model.getClient().getName());
        entity.setClient(ce);
    }
    return entity;
  }

  private BankAccount toAccountModel(BankAccountEntity entity) {
    if (entity == null) return null;
    BankAccount model = new BankAccount();
    model.setId(entity.getId());
    model.setAccountNumber(entity.getAccountNumber());
    model.setAccountType(entity.getAccountType());
    model.setAccountStatus(entity.getAccountStatus());
    model.setCurrency(entity.getCurrency());
    model.setCurrentBalance(entity.getCurrentBalance());
    model.setOpeningDate(entity.getOpeningDate());
    if (entity.getClient() != null) {
        PersonClient c = new PersonClient();
        c.setId(entity.getClient().getId());
        c.setName(entity.getClient().getName());
        model.setClient(c);
    }
    return model;
  }
}
