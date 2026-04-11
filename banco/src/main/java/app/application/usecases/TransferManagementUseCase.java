package app.application.usecases;

import app.domain.models.Transfer;

public interface TransferManagementUseCase {
    Transfer createTransfer(Transfer transfer);
    
    /**
     * CORRECCIÓN 4: Firma del método para aprobación de transferencias.
     * @param transferId ID de la transferencia a aprobar.
     * @param userId ID del usuario que aprueba (para auditoría).
     * @return Transferencia aprobada.
     */
    Transfer approveTransfer(Long transferId, Long userId);
    
    Transfer getTransferById(Long id);
}
