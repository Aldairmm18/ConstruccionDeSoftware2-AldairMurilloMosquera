package app.domain.services;

import app.domain.Exceptions.BusinessException;
import app.domain.Exceptions.PrestamoRechazadoException;
import app.domain.models.Loan;
import app.domain.models.LoanStatus;
import app.domain.models.OperationsLog;
import app.domain.ports.LoanPort;
import app.domain.ports.BankAccountPort;
import app.domain.ports.OperationsLogPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Consolidated Loan Service.
 * Manages the full lifecycle of loans from application to disbursement.
 */
@Service
@RequiredArgsConstructor
public class PrestamoService {
    
    private final LoanPort loanPort;
    private final app.domain.ports.ClientPort clientPort;
    private final BankAccountPort bankAccountPort;
    private final OperationsLogPort operationsLogPort;
    private final TransaccionService transaccionService;
    
    @Transactional
    public Loan solicitar(Loan prestamo) {
        // Validar cliente existe
        if (prestamo.getClient() == null || prestamo.getClient().getId() == null) {
            throw new BusinessException("Datos de cliente incompletos");
        }
        
        // REGLA: Cliente debe tener >= 2 cuentas ACTIVAS
        long cuentasActivas = bankAccountPort.countByClientId(prestamo.getClient().getId());
        if (cuentasActivas < 2) {
            throw new app.domain.Exceptions.PrestamoRechazadoException(
                String.format("Debe tener al menos 2 cuentas activas para solicitar préstamo. Tiene: %d", cuentasActivas));
        }
        
        // Validar monto
        if (prestamo.getRequestedAmount() == null || prestamo.getRequestedAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new app.domain.Exceptions.InvalidAmountException("Monto del préstamo debe ser mayor a 0");
        }
        
        // Validar cuenta de desembolso existe
        BankAccount cuentaDesembolso = bankAccountPort.findByAccountNumber(prestamo.getDisbursementTargetAccount().getAccountNumber());
        if (cuentaDesembolso == null) {
            throw new BusinessException("Cuenta de desembolso no encontrada");
        }
        
        // Validar que la cuenta pertenezca al cliente
        if (!cuentaDesembolso.getClient().getId().equals(prestamo.getClient().getId())) {
            throw new IllegalArgumentException("La cuenta de desembolso debe pertenecer al cliente");
        }
        
        // Estado inicial
        prestamo.setLoanStatus(LoanStatus.UNDER_REVIEW); // Using UNDER_REVIEW as equivalent to SOLICITADO
        
        Loan guardado = loanPort.save(prestamo);
        
        // Log
        registrarLog("PRESTAMO_SOLICITADO", guardado, null);
        
        return guardado;
    }
    
    @Transactional
    public Loan aprobar(Long prestamoId, Long usuarioId) {
        Loan prestamo = loanPort.findById(prestamoId);
        if (prestamo == null) {
            throw new RuntimeException("Préstamo no encontrado");
        }
        
        if (prestamo.getLoanStatus() != LoanStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Solo se pueden aprobar préstamos SOLICITADOS");
        }
        
        prestamo.setLoanStatus(LoanStatus.APPROVED);
        
        Loan actualizado = loanPort.save(prestamo);
        
        // Log
        registrarLog("PRESTAMO_APROBADO", actualizado, String.valueOf(usuarioId));
        
        return actualizado;
    }
    
    @Transactional
    public Loan desembolsar(Long prestamoId) {
        Loan prestamo = loanPort.findById(prestamoId);
        if (prestamo == null) {
            throw new RuntimeException("Préstamo no encontrado");
        }
        
        if (prestamo.getLoanStatus() != LoanStatus.APPROVED) {
            throw new IllegalStateException("Solo se pueden desembolsar préstamos APROBADOS");
        }
        
        // Acreditar monto a cuenta usando TransaccionService
        transaccionService.depositar(
            prestamo.getDisbursementTargetAccount().getAccountNumber(), 
            prestamo.getRequestedAmount()
        );
        
        prestamo.setLoanStatus(LoanStatus.ACTIVE); // Equivalent to DESEMBOLSADO
        prestamo.setDisbursementDate(LocalDate.now());
        
        Loan actualizado = loanPort.save(prestamo);
        
        // Log
        registrarLog("PRESTAMO_DESEMBOLSADO", actualizado, null);
        
        return actualizado;
    }
    
    public List<Loan> listarTodos() {
        return loanPort.findAll();
    }
    
    public Loan buscarPorId(Long id) {
        return loanPort.findById(id);
    }
    
    private void registrarLog(String operacion, Loan p, String usuarioId) {
        OperationsLog log = new OperationsLog();
        log.setLogId(UUID.randomUUID().toString());
        log.setOperationDateTime(LocalDateTime.now());
        log.setOperationType(operacion);
        
        Map<String, Object> detalles = new HashMap<>();
        detalles.put("prestamoId", p.getId());
        if (p.getClient() != null) detalles.put("clienteId", p.getClient().getId());
        detalles.put("monto", p.getRequestedAmount());
        detalles.put("estado", p.getLoanStatus().toString());
        if (usuarioId != null) {
            detalles.put("usuarioId", usuarioId);
        }
        log.setDetailData(detalles);
        
        operationsLogPort.save(log);
    }
}
