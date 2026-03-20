package app.application.usecases;

import app.domain.models.Transfer;

public interface TransferManagementUseCase {
    Transfer createTransfer(Transfer transfer);
    Transfer approveTransfer(Long transferId);
}
