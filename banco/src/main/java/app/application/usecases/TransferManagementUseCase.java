package app.application.usecases;

import app.domain.models.Transfer;
import java.util.List;

public interface TransferManagementUseCase {
    Transfer createTransfer(Transfer transfer);

    /**
     * Firma del metodo para aprobacion de transferencias.
     * @param transferId ID de la transferencia a aprobar.
     * @param userId ID del usuario que aprueba (para auditoria).
     * @return Transferencia aprobada.
     */
    Transfer approveTransfer(Long transferId, Long userId);

    Transfer getTransferById(Long id);

    List<Transfer> findAll();
}
