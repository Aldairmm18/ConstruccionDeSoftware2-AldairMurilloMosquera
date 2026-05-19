package app.application.usecases;

import app.domain.models.Transaction;
import app.domain.services.BillPaymentService;
import app.domain.services.DepositService;
import app.domain.services.TransactionQueryService;
import app.domain.services.WithdrawalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionManagementUseCaseImpl implements TransactionManagementUseCase {

    private final DepositService depositService;
    private final WithdrawalService withdrawalService;
    private final BillPaymentService billPaymentService;
    private final TransactionQueryService transactionQueryService;

    @Override
    @Transactional
    public Transaction makeDeposit(String accountNumber, BigDecimal amount, String description) {
        return depositService.executeDeposit(accountNumber, amount, description);
    }

    @Override
    @Transactional
    public Transaction makeWithdrawal(String accountNumber, BigDecimal amount, String description) {
        return withdrawalService.executeWithdrawal(accountNumber, amount, description);
    }

    @Override
    @Transactional
    public Transaction payService(String accountNumber, String serviceName, String reference, BigDecimal amount) {
        return billPaymentService.payBill(accountNumber, serviceName, reference, amount);
    }

    @Override
    public List<Transaction> getTransactionsByAccount(String accountNumber) {
        return transactionQueryService.findByAccountNumber(accountNumber);
    }
}
