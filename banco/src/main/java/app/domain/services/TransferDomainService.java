package app.domain.services;

import app.domain.models.Transfer;
import app.domain.Exceptions.BusinessException;
import app.domain.Exceptions.InsufficientFundsException;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

/**
 * Domain service for transfer validation and business rules.
 */
@Service
public class TransferDomainService {

    public void validateTransferCreation(Transfer transfer) {
        if (transfer.getSourceAccount() == null || transfer.getTargetAccount() == null) {
            throw new BusinessException("Source and target accounts are required.");
        }

        if (transfer.getSourceAccount().getId().equals(transfer.getTargetAccount().getId()) || 
            transfer.getSourceAccount().getAccountNumber().equals(transfer.getTargetAccount().getAccountNumber())) {
            throw new BusinessException("Source and target accounts cannot be the same.");
        }

        if (transfer.getAmount() == null || transfer.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero.");
        }

        if (transfer.getSourceAccount().getCurrentBalance().compareTo(transfer.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds for transfer.");
        }
    }
}
