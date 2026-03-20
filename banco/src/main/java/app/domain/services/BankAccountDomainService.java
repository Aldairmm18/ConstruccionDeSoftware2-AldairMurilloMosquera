package app.domain.services;

import app.domain.models.BankAccount;
import app.domain.ports.BankAccountPort;
import app.domain.ports.ClientPort;
import app.domain.Exceptions.BusinessException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BankAccountDomainService {

    private final BankAccountPort bankAccountPort;
    private final ClientPort clientPort;

    public void validateBankAccountCreation(BankAccount account) {
        if (account.getClient() == null || clientPort.findById(account.getClient().getId()) == null) {
            throw new BusinessException("El cliente proporcionado no existe.");
        }
        if (bankAccountPort.existsByAccountNumber(account.getAccountNumber())) {
            throw new BusinessException("El número de cuenta ya se encuentra registrado.");
        }
    }
}
