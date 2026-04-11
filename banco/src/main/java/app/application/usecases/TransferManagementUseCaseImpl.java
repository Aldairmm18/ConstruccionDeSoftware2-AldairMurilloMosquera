package app.application.usecases;

import app.domain.models.Transfer;
import app.domain.models.TransferStatus;
import app.domain.ports.TransferPort;
import app.domain.services.TransferDomainService;
import app.domain.Exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransferManagementUseCaseImpl implements TransferManagementUseCase {

    private final TransferPort transferPort;
    private final TransferDomainService transferDomainService;

    @Override
    @Transactional
    public Transfer createTransfer(Transfer transfer) {
        // Validación inicial del dominio
        transferDomainService.validateTransferCreation(transfer);
        return transferPort.save(transfer);
    }

    @Override
    @Transactional
    public Transfer approveTransfer(Long transferId, Long userId) {
        // 1. Buscar la transferencia por ID
        Transfer transfer = transferPort.findById(transferId);
        
        if (transfer == null) {
            throw new BusinessException("Transferencia no encontrada.");
        }

        // CORRECCIÓN 4: Invocar validateExpiry antes de proceder
        // Esto verifica si pasaron más de 60 min; si sí, lanza excepción y marca EXPIRED internamente
        transferDomainService.validateExpiry(transfer);

        // Validar que el estado permita la aprobación
        if (transfer.getTransferStatus() != TransferStatus.PENDING && 
            transfer.getTransferStatus() != TransferStatus.PENDING_APPROVAL) {
            throw new BusinessException("Solo se pueden aprobar transferencias en estado PENDIENTE.");
        }

        // 3. Cambiar estado a APPROVED y setear fecha de aprobación
        transfer.setTransferStatus(TransferStatus.APPROVED);
        transfer.setApprovalDate(LocalDateTime.now());
        
        // El requerimiento no pide setear el approverUser aquí, pero sería buena práctica. 
        // Por ahora seguimos la lógica solicitada de modificar estado y fecha.

        // 4. Guardar y retornar
        return transferPort.save(transfer);
    }

    @Override
    public Transfer getTransferById(Long id) {
        // CORRECCIÓN 4: Al consultar una transferencia, también validar si ha vencido
        Transfer transfer = transferPort.findById(id);
        if (transfer != null) {
            try {
                transferDomainService.validateExpiry(transfer);
            } catch (BusinessException e) {
                // Si venció al consultar, guardamos el estado actualizado
                transferPort.save(transfer);
            }
        }
        return transfer;
    }
}
