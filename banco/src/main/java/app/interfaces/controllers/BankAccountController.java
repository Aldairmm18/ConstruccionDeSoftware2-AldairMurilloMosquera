package app.interfaces.controllers;

import app.application.usecases.AccountManagementUseCase;
import app.domain.models.BankAccount;
import app.domain.models.Client;
import app.domain.ports.BankAccountPort;
import app.interfaces.controllers.requests.AccountRequest;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class BankAccountController {

  private final AccountManagementUseCase accountManagementUseCase;
  private final BankAccountPort bankAccountPort;

  @PostMapping
  public ResponseEntity<BankAccount> create(@Valid @RequestBody AccountRequest request) {
    BankAccount account = toModel(request);
    BankAccount createdAccount = accountManagementUseCase.createAccount(account);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
  }

  @GetMapping("/{id}")
  public ResponseEntity<BankAccount> findById(@PathVariable Long id) {
    BankAccount account = bankAccountPort.findById(id);
    if (account == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(account);
  }

  @GetMapping
  public ResponseEntity<List<BankAccount>> findAll() {
    return ResponseEntity.ok(bankAccountPort.findAll());
  }

  private BankAccount toModel(AccountRequest request) {
    BankAccount account = new BankAccount();
    account.setAccountType(request.getAccountType());
    if (request.getCurrency() != null) {
        account.setCurrency(app.domain.models.Currency.valueOf(request.getCurrency()));
    }
    account.setAccountNumber(request.getAccountNumber());
    account.setCurrentBalance(request.getCurrentBalance());
    account.setOpeningDate(java.time.LocalDate.now());
    
    if (request.getClientId() != null) {
        Client client = new Client();
        client.setId(request.getClientId());
        account.setClient(client);
    }
    return account;
  }
}
