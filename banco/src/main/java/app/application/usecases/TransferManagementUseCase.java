package app.application.usecases;

import app.domain.models.Transfer;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TransferManagementUseCase {
    Transfer requestTransfer(String sourceAccountNumber, String targetAccountNumber, BigDecimal amount);
    Transfer approveTransfer(String transferId, String auditorId);
    Transfer rejectTransfer(String transferId, String reason);
    List<Transfer> findPendingTransfers();
    Optional<Transfer> findById(String id);
    List<Transfer> findAll();
}
