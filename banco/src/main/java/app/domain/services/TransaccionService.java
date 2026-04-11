package app.domain.services;

import app.domain.Exceptions.BusinessException;
import app.domain.Exceptions.InvalidAmountException;
import app.domain.models.BankAccount;
import app.domain.models.OperationsLog;
import app.domain.models.Transfer;
import app.domain.models.TransferStatus;
import app.domain.ports.BankAccountPort;
import app.domain.ports.OperationsLogPort;
import app.domain.ports.TransferPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransaccionService {

    private final BankAccountPort bankAccountPort;
    private final OperationsLogPort operationsLogPort;
    private final TransferPort transferPort;

    @Transactional
    public void depositar(String numeroCuenta, BigDecimal monto) {
        validarMonto(monto);
        BankAccount cuenta = bankAccountPort.findByAccountNumberForUpdate(numeroCuenta);
        if (cuenta == null) throw new BusinessException("Cuenta no encontrada");

        BigDecimal saldoAnterior = cuenta.getCurrentBalance();
        cuenta.acreditar(monto);
        bankAccountPort.save(cuenta);

        registrarLog("DEPOSITO", cuenta, monto, saldoAnterior);
        recordLedger(null, cuenta, monto);
    }

    @Transactional
    public void retirar(String numeroCuenta, BigDecimal monto) {
        validarMonto(monto);
        BankAccount cuenta = bankAccountPort.findByAccountNumberForUpdate(numeroCuenta);
        if (cuenta == null) throw new BusinessException("Cuenta no encontrada");

        BigDecimal saldoAnterior = cuenta.getCurrentBalance();
        cuenta.debitar(monto);
        bankAccountPort.save(cuenta);

        registrarLog("RETIRO", cuenta, monto, saldoAnterior);
        recordLedger(cuenta, null, monto);
    }

    private void validarMonto(BigDecimal monto) {
        if (monto == null) {
            throw new InvalidAmountException("Monto no puede ser nulo");
        }
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Monto debe ser mayor a 0");
        }
        if (monto.compareTo(new BigDecimal("999999999.99")) > 0) {
            throw new InvalidAmountException("Monto excede límite permitido");
        }
        if (monto.scale() > 2) {
            throw new InvalidAmountException("Monto no puede tener más de 2 decimales");
        }
    }

    private void registrarLog(String tipo, BankAccount cuenta, BigDecimal monto, BigDecimal saldoAnterior) {
        OperationsLog log = new OperationsLog();
        log.setLogId(UUID.randomUUID().toString());
        log.setOperationDateTime(LocalDateTime.now());
        log.setOperationType(tipo);

        Map<String, Object> detalles = new HashMap<>();
        detalles.put("cuentaId", cuenta.getAccountNumber());
        detalles.put("monto", monto);
        detalles.put("saldoAnterior", saldoAnterior);
        detalles.put("saldoNuevo", cuenta.getCurrentBalance());
        log.setDetailData(detalles);

        operationsLogPort.save(log);
    }

    private void recordLedger(BankAccount source, BankAccount target, BigDecimal amount) {
        Transfer record = new Transfer();
        record.setSourceAccount(source);
        record.setTargetAccount(target);
        record.setAmount(amount);
        record.setTransferStatus(TransferStatus.EXECUTED);
        record.setCreationDate(LocalDateTime.now());
        record.setApprovalDate(LocalDateTime.now());
        transferPort.save(record);
    }
}
