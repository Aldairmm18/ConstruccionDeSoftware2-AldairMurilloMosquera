package app.interfaces.controllers.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class LoanRequest {
    @NotNull(message = "El ID del cliente solicitante no puede estar en blanco")
    private Long requestingClientId;

    @Positive(message = "El monto solicitado debe ser numéricamente positivo")
    private Double requestedAmount;

    @Min(value = 1, message = "El plazo en meses debe ser de al menos 1")
    private Integer termMonths;

    private Long disbursementTargetAccountId;
    private String loanType;
}
