package app.application.usecases;

import app.domain.models.Transfer;
import app.domain.models.TransferStatus;
import app.domain.ports.TransferPort;
import app.domain.services.TransferDomainService;
import app.domain.Exceptions.BusinessException;
import app.domain.Exceptions.UnauthorizedAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
    @Transactional
    public Transfer approveTransfer(Long transferId) {
        checkAuthorization("APPROVE_TRANSFER");
        
        Transfer transfer = transferPort.findById(transferId);
        
        if (transfer == null) {
            throw new BusinessException("Transfer not found");
        }
        
        if (transfer.getTransferStatus() != TransferStatus.PENDING) {
            throw new BusinessException("Only PENDING transfers can be approved");
        }
        
        transfer.setTransferStatus(TransferStatus.APPROVED);
        
        return transferPort.save(transfer);
    }


    @Scheduled(fixedRate = 60000)
    @Transactional
    public void expireOldTransfers() {
        LocalDateTime expirationThreshold = LocalDateTime.now().minusMinutes(60);
        
        List<Transfer> pendingTransfers = transferPort.findByTransferStatusAndCreationDateBefore(
                TransferStatus.PENDING, expirationThreshold);
        
        for (Transfer transfer : pendingTransfers) {
            transfer.setTransferStatus(TransferStatus.EXPIRED);
            transferPort.save(transfer);
        }
    }


    private void checkAuthorization(String action) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null) {
            throw new UnauthorizedAccessException("User not authenticated");
        }
        
        String currentRole = authentication.getAuthorities().iterator().next().getAuthority();
        
        if (action.equals("APPROVE_TRANSFER") && currentRole.equals("ROLE_TELLER_EMPLOYEE")) {
            throw new UnauthorizedAccessException("Access denied: TELLER_EMPLOYEE cannot approve transfers");
        }
    }

}
