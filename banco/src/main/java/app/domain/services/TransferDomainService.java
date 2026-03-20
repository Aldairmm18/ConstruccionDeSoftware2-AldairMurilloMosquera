package app.domain.services;

import app.domain.models.Transfer;
import app.domain.Exceptions.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class TransferDomainService {

    public void validateTransferCreation(Transfer transfer) {
        if (transfer.getSourceAccount() == null || transfer.getTargetAccount() == null) {
            throw new BusinessException("Cuenta origen y destino son requeridas.");
        }
        if (transfer.getSourceAccount().getId().equals(transfer.getTargetAccount().getId()) || 
            transfer.getSourceAccount().getAccountNumber().equals(transfer.getTargetAccount().getAccountNumber())) {
            throw new BusinessException("La cuenta origen y destino no pueden ser iguales.");
        }
        if (transfer.getAmount() == null || transfer.getAmount() <= 0) {
            throw new BusinessException("El monto a transferir debe ser mayor a 0.");
        }
    }
}
