package app.application.usecases;

import app.domain.models.BankAccount;
import app.domain.models.AccountStatus;
import app.domain.ports.BankAccountPort;
import app.domain.services.BankAccountDomainService;
import app.domain.Exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountManagementUseCaseImpl implements AccountManagementUseCase {

    private final BankAccountPort bankAccountPort;
    private final BankAccountDomainService bankAccountDomainService;

    @Override
    @Transactional
    public BankAccount createAccount(BankAccount account) {
        if (account.getAccountNumber() == null) {
            // Generacion con reintentos para reducir colisiones
            int attempts = 0;
            do {
                account.setAccountNumber(generateAccountNumber());
                attempts++;
            } while (bankAccountPort.existsByAccountNumber(account.getAccountNumber()) && attempts < 5);

            if (bankAccountPort.existsByAccountNumber(account.getAccountNumber())) {
                throw new BusinessException("No se pudo generar un numero de cuenta unico.");
            }
        }

        // Valores por defecto antes de validar y persistir
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setOpeningDate(LocalDate.now());
        if (account.getCurrentBalance() == null) {
            account.setCurrentBalance(BigDecimal.ZERO);
        }

        // Validaciones de dominio centralizadas
        bankAccountDomainService.validateBankAccountCreation(account);
        return bankAccountPort.save(account);
    }

    @Override
    @Transactional
    public BankAccount blockAccount(Long accountId) {
        BankAccount account = bankAccountPort.findById(accountId);
        if (account == null) {
            throw new BusinessException("Cuenta no encontrada.");
        }
        account.setAccountStatus(AccountStatus.BLOCKED);
        return bankAccountPort.save(account);
    }

    @Override
    public List<BankAccount> findAll() {
        return bankAccountPort.findAll();
    }

    @Override
    public BankAccount findById(Long id) {
        return bankAccountPort.findById(id);
    }

    @Override
    public List<BankAccount> findByClientId(Long clientId) {
        return bankAccountPort.findByClientId(clientId);
    }

    private String generateAccountNumber() {
        // Generador mas seguro: prefijo + timestamp parcial + aleatorio de 4 digitos
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(5);
        String random = String.format("%04d", new Random().nextInt(9999));
        return "COL" + timestamp + random;
    }
}
