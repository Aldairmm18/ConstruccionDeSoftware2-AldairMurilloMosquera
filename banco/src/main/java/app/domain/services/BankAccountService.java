package app.domain.services;

import app.domain.Exceptions.BusinessException;
import app.domain.models.AccountStatus;
import app.domain.models.BankAccount;
import app.domain.models.PersonClient;
import app.domain.ports.BankAccountPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;
import java.util.List;

/**
 * Consolidated Bank Account Service.
 */
@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final BankAccountPort bankAccountPort;
    private final BankAccountDomainService bankAccountDomainService;

    public BankAccount openAccount(BankAccount account) {
        if (account.getAccountNumber() == null) {
            account.setAccountNumber(generateAccountNumber());
        }
        
        if (bankAccountPort.existsByAccountNumber(account.getAccountNumber())) {
            throw new BusinessException("Account number already exists");
        }

        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setOpeningDate(LocalDate.now());
        if (account.getCurrentBalance() == null) {
            account.setCurrentBalance(BigDecimal.ZERO);
        }

        return bankAccountPort.save(account);
    }

    public BankAccount findById(Long id) {
        return bankAccountPort.findById(id);
    }

    public List<BankAccount> findByClientId(Long clientId) {
        return bankAccountPort.findByClientId(clientId);
    }

    private String generateAccountNumber() {
        // Simple 10-digit account generator
        return String.format("%010d", new Random().nextInt(1000000000));
    }
}
