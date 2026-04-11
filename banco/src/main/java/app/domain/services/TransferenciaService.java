package app.domain.services;

import app.domain.Exceptions.*;
import app.domain.models.*;
import app.domain.ports.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TransferenciaService {
    
    private final TransferPort transferPort;
    private final BankAccountPort bankAccountPort;
    private final OperationsLogPort operationsLogPort;
    private final TransferDomainService transferDomainService;
    
    @Transactional
    public Transfer crear(Transfer transferencia) {
        // Validar monto
        validarMonto(transferencia.getAmount());
        
        // Validar cuentas diferentes
        if (transferencia.getSourceAccount().getAccountNumber()
                .equals(transferencia.getTargetAccount().getAccountNumber())) {
            throw new IllegalArgumentException("Cuenta origen y destino deben ser diferentes");
        }
        
        // Validar saldo suficiente
        BankAccount origen = bankAccountPort.findByAccountNumberForUpdate(transferencia.getSourceAccount().getAccountNumber());
        if (origen == null) throw new BusinessException("Cuenta origen no encontrada");
        
        if (origen.getCurrentBalance().compareTo(transferencia.getAmount()) < 0) {
            throw new InsufficientFundsException(
                String.format("Saldo insuficiente. Disponible: %s, Requerido: %s",
                    origen.getCurrentBalance(), transferencia.getAmount()));
        }
        
        // Verificar cuenta destino existe
        BankAccount destino = bankAccountPort.findByAccountNumber(transferencia.getTargetAccount().getAccountNumber());
        if (destino == null) throw new BusinessException("Cuenta destino no encontrada");
        
        // Crear con estado PENDIENTE y fechas
        transferencia.setTransferStatus(TransferStatus.PENDING);
        transferencia.setCreationDate(LocalDateTime.now());
        // expirationDate is set in Transfer constructor as per my previous edit
        
        Transfer guardada = transferPort.save(transferencia);
        
        // Log
        registrarLog("TRANSFERENCIA_CREADA", guardada, null);
        
        return guardada;
    }
    
    @Transactional
    public Transfer aprobar(Long transferenciaId, Long usuarioId) {
        Transfer t = transferPort.findById(transferenciaId);
        if (t == null) throw new RuntimeException("Transferencia no encontrada");
        
        if (t.isExpired()) {
            t.setTransferStatus(TransferStatus.EXPIRED);
            transferPort.save(t);
            throw new TransferenciaExpiradaException("La transferencia expiró (límite: 60 minutos)");
        }
        
        if (t.getTransferStatus() != TransferStatus.PENDING) {
            throw new IllegalStateException("Solo se pueden aprobar transferencias PENDIENTES");
        }
        
        // Obtener cuentas con bloqueo
        BankAccount origen = bankAccountPort.findByAccountNumberForUpdate(t.getSourceAccount().getAccountNumber());
        BankAccount destino = bankAccountPort.findByAccountNumberForUpdate(t.getTargetAccount().getAccountNumber());
        
        // Ejecutar transferencia con métodos blindados
        origen.debitar(t.getAmount());
        destino.acreditar(t.getAmount());
        
        bankAccountPort.save(origen);
        bankAccountPort.save(destino);
        
        // Cambiar estado
        t.setTransferStatus(TransferStatus.APPROVED);
        t.setApprovalDate(LocalDateTime.now());
        t.setApproverUserId(usuarioId);
        
        Transfer actualizada = transferPort.save(t);
        
        // Log
        registrarLog("TRANSFERENCIA_APROBADA", actualizada, String.valueOf(usuarioId));
        
        return actualizada;
    }
    
    public List<Transfer> listarTodas() {
        return transferPort.findAll();
    }
    
    public Transfer buscarPorId(Long id) {
        return transferPort.findById(id);
    }
    
    @Scheduled(fixedRate = 60000) // Cada minuto
    @Transactional
    public void expirarTransferenciasPendientes() {
        List<Transfer> pendientes = transferPort.findByTransferStatus(TransferStatus.PENDING);
        
        for (Transfer t : pendientes) {
            if (t.isExpired()) {
                t.setTransferStatus(TransferStatus.EXPIRED);
                transferPort.save(t);
                registrarLog("TRANSFERENCIA_EXPIRADA_AUTOMATICAMENTE", t, "SYSTEM");
            }
        }
    }
    
    private void validarMonto(BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Monto debe ser mayor a 0");
        }
        if (monto.scale() > 2) {
            throw new InvalidAmountException("Monto no puede tener más de 2 decimales");
        }
    }
    
    private void registrarLog(String operacion, Transfer t, String usuarioId) {
        OperationsLog log = new OperationsLog();
        log.setLogId(UUID.randomUUID().toString());
        log.setOperationDateTime(LocalDateTime.now());
        log.setOperationType(operacion);
        
        Map<String, Object> detalles = new HashMap<>();
        detalles.put("transferenciaId", t.getId());
        detalles.put("cuentaOrigenId", t.getSourceAccount().getAccountNumber());
        detalles.put("cuentaDestinoId", t.getTargetAccount().getAccountNumber());
        detalles.put("monto", t.getAmount());
        detalles.put("estado", t.getTransferStatus().toString());
        if (usuarioId != null) {
            detalles.put("usuarioId", usuarioId);
        }
        log.setDetailData(detalles);
        
        operationsLogPort.save(log);
    }
}
