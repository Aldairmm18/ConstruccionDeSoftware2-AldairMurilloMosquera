package app.domain.services;

import app.domain.models.Loan;
import java.math.BigDecimal;
import app.domain.Exceptions.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class LoanDomainService {

    public void validateLoanCreation(Loan loan) {
        if (loan.getRequestedAmount() == null
            || loan.getRequestedAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto solicitado debe ser mayor a 0.");
        }
        if (loan.getTermMonths() <= 0) {
            throw new BusinessException("El plazo en meses debe ser mayor a 0.");
        }
    }
}
