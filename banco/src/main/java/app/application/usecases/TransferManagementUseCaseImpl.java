package app.application.usecases;

import app.domain.models.Transfer;
import app.domain.models.TransferStatus;
import app.domain.ports.TransferPort;
import app.domain.services.TransferDomainService;
import app.domain.Exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferManagementUseCaseImpl implements TransferManagementUseCase {

    private final TransferPort transferPort;
    private final TransferDomainService transferDomainService;

    @Override
    public Transfer createTransfer(Transfer transfer) {
        transferDomainService.validateTransferCreation(transfer);
        transfer.setTransferStatus(TransferStatus.PENDING);
        return transferPort.save(transfer);
    }

    @Override
    public Transfer approveTransfer(Long transferId) {
        Transfer transfer = transferPort.findById(transferId);
        if (transfer == null) {
            throw new BusinessException("Transferencia no encontrada.");
        }
        transfer.setTransferStatus(TransferStatus.APPROVED);
        return transferPort.save(transfer);
    }
}
