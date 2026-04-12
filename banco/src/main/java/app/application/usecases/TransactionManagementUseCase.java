package app.application.usecases;

import java.math.BigDecimal;

public interface TransactionManagementUseCase {
    void deposit(String accountNumber, BigDecimal amount);
    void withdraw(String accountNumber, BigDecimal amount);
}
