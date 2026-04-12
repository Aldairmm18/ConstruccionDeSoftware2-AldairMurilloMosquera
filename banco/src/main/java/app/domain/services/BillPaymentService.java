package app.domain.services;

import app.domain.Exceptions.*;
import app.domain.models.*;
import app.domain.ports.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service for BILL PAYMENT OPERATIONS
 * Handles: Utility bill payments
 */
@Service
public class BillPaymentService {
    
    @Autowired
    private BankAccountRepository accountRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private ValidationService validationService;
    
    @Autowired
    private AuditService auditService;
    
    /**
     * Pays a utility bill from an account
     */
    @Transactional
    public Transaction payBill(
            String accountNumber,
            String serviceName,
            String reference,
            BigDecimal amount) {
        
        validationService.validateAmount(amount);
        
        BankAccount account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new BusinessException(
                "Cuenta no encontrada: " + accountNumber));
        
        BigDecimal previousBalance = account.getBalance();
        
        account.debit(amount);
        accountRepository.save(account);
        
        String description = String.format("Pago %s - Ref: %s", serviceName, reference);
        
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.SERVICE_PAYMENT);
        transaction.setDate(LocalDateTime.now());
        transaction.setDescription(description);
        
        Transaction saved = transactionRepository.save(transaction);
        auditService.logTransaction(saved, previousBalance, account.getBalance());
        
        return saved;
    }
}
