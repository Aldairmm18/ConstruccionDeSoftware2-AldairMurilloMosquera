package app.infrastructure.config;

import app.domain.ports.*;
import app.domain.services.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServicesConfig {

    @Bean
    public ValidationService validationService() {
        return new ValidationService();
    }

    @Bean
    public AuditService auditService(OperationsLogPort operationsLogPort) {
        return new AuditService(operationsLogPort);
    }

    @Bean
    public ClientService clientService(ClientPort clientPort, AuditService auditService) {
        return new ClientService(clientPort, auditService);
    }

    @Bean
    public AccountService accountService(BankAccountPort bankAccountPort, ClientPort clientPort,
                                         AuditService auditService) {
        return new AccountService(bankAccountPort, clientPort, auditService);
    }

    @Bean
    public DepositService depositService(BankAccountPort bankAccountPort, TransactionPort transactionPort,
                                         ValidationService validationService, AuditService auditService) {
        return new DepositService(bankAccountPort, transactionPort, validationService, auditService);
    }

    @Bean
    public WithdrawalService withdrawalService(BankAccountPort bankAccountPort, TransactionPort transactionPort,
                                               ValidationService validationService, AuditService auditService) {
        return new WithdrawalService(bankAccountPort, transactionPort, validationService, auditService);
    }

    @Bean
    public BillPaymentService billPaymentService(BankAccountPort bankAccountPort, TransactionPort transactionPort,
                                                 ValidationService validationService, AuditService auditService) {
        return new BillPaymentService(bankAccountPort, transactionPort, validationService, auditService);
    }

    @Bean
    public TransactionQueryService transactionQueryService(TransactionPort transactionPort,
                                                           BankAccountPort bankAccountPort) {
        return new TransactionQueryService(transactionPort, bankAccountPort);
    }

    @Bean
    public LoanService loanService(LoanPort loanPort, ClientPort clientPort,
                                   BankAccountPort bankAccountPort, AuditService auditService) {
        return new LoanService(loanPort, clientPort, bankAccountPort, auditService);
    }

    @Bean
    public TransferService transferService(TransferPort transferPort, BankAccountPort bankAccountPort,
                                           ValidationService validationService, AuditService auditService) {
        return new TransferService(transferPort, bankAccountPort, validationService, auditService);
    }
}
