package app.domain.services;

import app.domain.models.Transfer;
import app.domain.models.TransferStatus;
import app.domain.Exceptions.BusinessException;
import app.domain.Exceptions.InsufficientFundsException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

/**
 * Servicio de dominio para validación de transferencias y reglas de negocio.
 */
@Service
public class TransferDomainService {

    private static final BigDecimal HIGH_AMOUNT_THRESHOLD = new BigDecimal("10000000");

    public void validateTransferCreation(Transfer transfer) {
        if (transfer.getSourceAccount() == null || transfer.getTargetAccount() == null) {
            throw new BusinessException("Las cuentas de origen y destino son obligatorias.");
        }

        if (transfer.getSourceAccount().getId().equals(transfer.getTargetAccount().getId()) || 
            transfer.getSourceAccount().getAccountNumber().equals(transfer.getTargetAccount().getAccountNumber())) {
            throw new BusinessException("La cuenta de origen y destino no pueden ser la misma.");
        }

        if (transfer.getAmount() == null || transfer.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto de la transferencia debe ser mayor a cero.");
        }

        if (transfer.getSourceAccount().getCurrentBalance().compareTo(transfer.getAmount()) < 0) {
            throw new InsufficientFundsException("Saldo insuficiente para realizar la transferencia.");
        }

        // CORRECCIÓN 1: Validación de alto monto
        // Si el monto supera los 10 millones, requiere aprobación
        if (transfer.getAmount().compareTo(HIGH_AMOUNT_THRESHOLD) > 0) {
            transfer.setTransferStatus(TransferStatus.PENDING_APPROVAL);
        } else {
            transfer.setTransferStatus(TransferStatus.PENDING);
        }
        
        // Registrar fecha de creación para control de vencimiento
        transfer.setCreationDate(LocalDateTime.now());
    }

    /**
     * Valida si la transferencia ha vencido (más de 60 minutos sin aprobación).
     */
    public void validateExpiry(Transfer transfer) {
        if (transfer.getCreationDate() == null) return;
        
        if (LocalDateTime.now().isAfter(transfer.getCreationDate().plusMinutes(60))) {
            transfer.setTransferStatus(TransferStatus.EXPIRED);
            throw new BusinessException("La transferencia ha vencido por falta de aprobación (límite 60 min).");
        }
    }
}
