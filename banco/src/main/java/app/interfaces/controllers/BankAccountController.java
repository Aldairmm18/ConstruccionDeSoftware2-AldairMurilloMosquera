package app.interfaces.controllers;

import app.application.services.BankAccountService;
import app.domain.models.BankAccount;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class BankAccountController {

  private final BankAccountService bankAccountService;

  @PostMapping
  public BankAccount create(@RequestBody BankAccount bankAccount) {
    return bankAccountService.create(bankAccount);
  }

  @GetMapping("/{id}")
  public BankAccount findById(@PathVariable Long id) {
    return bankAccountService.findById(id);
  }

  @GetMapping
  public List<BankAccount> findAll() {
    return bankAccountService.findAll();
  }
}
