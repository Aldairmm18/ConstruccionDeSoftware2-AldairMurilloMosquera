package app.application.services;

import app.domain.models.BankAccount;
import app.domain.ports.BankAccountPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

  private final BankAccountPort bankAccountPort;

  @Override
  public BankAccount create(BankAccount bankAccount) {
    return bankAccountPort.save(bankAccount);
  }

  @Override
  public BankAccount findById(Long id) {
    return bankAccountPort.findById(id);
  }

  @Override
  public List<BankAccount> findAll() {
    return bankAccountPort.findAll();
  }
}
