package app.application.adapters.persistence.sql;

import app.application.adapters.persistence.sql.entities.BankAccountEntity;
import app.application.adapters.persistence.sql.entities.ClientEntity;
import app.application.adapters.persistence.sql.repositories.BankAccountRepository;
import app.domain.models.BankAccount;
import app.domain.models.PersonClient;
import app.domain.ports.BankAccountPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankAccountPersistenceAdapter implements BankAccountPort {

  private final BankAccountRepository bankAccountRepository;

  @Override
  public BankAccount save(BankAccount bankAccount) {
    BankAccountEntity entity = toEntity(bankAccount);
    if (entity != null && (entity.getId() == null || entity.getId() == 0L)) {
      entity.setId(null);
    }
    BankAccountEntity saved = bankAccountRepository.save(entity);
    return toModel(saved);
  }

  @Override
  public BankAccount findById(Long id) {
    return bankAccountRepository.findById(id).map(this::toModel).orElse(null);
  }

  @Override
  public List<BankAccount> findAll() {
    return bankAccountRepository.findAll().stream().map(this::toModel).toList();
  }

  @Override
  public BankAccount findByAccountNumber(String accountNumber) {
    return bankAccountRepository.findByAccountNumber(accountNumber).map(this::toModel).orElse(null);
  }

  @Override
  public BankAccount findByAccountNumberForUpdate(String accountNumber) {
    return bankAccountRepository.findByAccountNumberForUpdate(accountNumber).map(this::toModel).orElse(null);
  }

  @Override
  public boolean existsByAccountNumber(String accountNumber) {
    return bankAccountRepository.existsByAccountNumber(accountNumber);
  }

  @Override
  public List<BankAccount> findByClientId(Long clientId) {
    return bankAccountRepository.findByClient_Id(clientId).stream().map(this::toModel).toList();
  }

  @Override
  public long countByClientId(Long clientId) {
    return bankAccountRepository.countByClient_Id(clientId);
  }

  private BankAccountEntity toEntity(BankAccount model) {
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
      ClientEntity clientEntity = new ClientEntity();
      clientEntity.setId(model.getClient().getId());
      clientEntity.setName(model.getClient().getName());
      clientEntity.setDocument(model.getClient().getDocument());
      clientEntity.setEmail(model.getClient().getEmail());
      clientEntity.setPhone(model.getClient().getPhone());
      clientEntity.setAddress(model.getClient().getAddress());
      clientEntity.setBirthDate(model.getClient().getBirthDate());
      entity.setClient(clientEntity);
    }
    return entity;
  }

  private BankAccount toModel(BankAccountEntity entity) {
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
      PersonClient client = new PersonClient();
      client.setId(entity.getClient().getId());
      client.setName(entity.getClient().getName());
      client.setDocument(entity.getClient().getDocument());
      client.setEmail(entity.getClient().getEmail());
      client.setPhone(entity.getClient().getPhone());
      client.setAddress(entity.getClient().getAddress());
      client.setBirthDate(entity.getClient().getBirthDate());
      model.setClient(client);
    }
    return model;
  }
}
