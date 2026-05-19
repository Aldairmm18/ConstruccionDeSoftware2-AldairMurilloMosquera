package app.application.usecases;

import app.domain.Exceptions.AccessDeniedException;
import app.domain.Exceptions.BusinessException;
import app.domain.models.SystemRole;
import app.domain.models.Transfer;
import app.domain.models.User;
import app.domain.ports.UserPort;
import app.domain.services.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransferManagementUseCaseImpl implements TransferManagementUseCase {

    private final TransferService transferService;
    private final UserPort userPort;

    @Override
    @Transactional
    public Transfer requestTransfer(String sourceAccountNumber, String targetAccountNumber, BigDecimal amount) {
        return transferService.requestTransfer(sourceAccountNumber, targetAccountNumber, amount);
    }

    @Override
    @Transactional
    public Transfer approveTransfer(String transferId, String auditorId) {
        requireSupervisorOrAnalyst(auditorId);
        return transferService.approveTransfer(Long.parseLong(transferId), auditorId);
    }

    @Override
    @Transactional
    public Transfer rejectTransfer(String transferId, String auditorId, String reason) {
        requireSupervisorOrAnalyst(auditorId);
        return transferService.rejectTransfer(Long.parseLong(transferId), reason);
    }

    @Override
    public List<Transfer> findPendingTransfers() {
        return transferService.findPendingTransfers();
    }

    @Override
    public Optional<Transfer> findById(String id) {
        return Optional.ofNullable(transferService.findById(Long.parseLong(id)));
    }

    @Override
    public List<Transfer> findAll() {
        return transferService.findAll();
    }

    @Scheduled(fixedRate = 60000)
    public void expirePendingTransfers() {
        transferService.expirePendingTransfers();
    }

    private void requireSupervisorOrAnalyst(String userId) {
        User user = userPort.findById(Long.parseLong(userId));
        if (user == null) throw new BusinessException("User not found");
        SystemRole role = user.getSystemRole();
        if (role != SystemRole.CORPORATE_SUPERVISOR && role != SystemRole.INTERNAL_ANALYST) {
            throw new AccessDeniedException(
                    "Only CORPORATE_SUPERVISOR or INTERNAL_ANALYST can process transfers");
        }
    }
}
