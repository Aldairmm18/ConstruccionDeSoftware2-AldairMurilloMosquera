package app.application.usecases;

import app.domain.models.Transaction;
import java.math.BigDecimal;
import java.util.List;

public interface TransactionManagementUseCase {
    Transaction makeDeposit(String accountNumber, BigDecimal amount, String description);
    Transaction makeWithdrawal(String accountNumber, BigDecimal amount, String description);
    Transaction payService(String accountNumber, String serviceName, String reference, BigDecimal amount);
    List<Transaction> getTransactionsByAccount(String accountNumber);
}
