package app.application.services;

import app.domain.models.BankAccount;
import java.util.List;

public interface BankAccountService {

  BankAccount create(BankAccount bankAccount);

  BankAccount findById(Long id);

  List<BankAccount> findAll();
}
