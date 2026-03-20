package app.interfaces.controllers.requests;

import jakarta.validation.constraints.NotNull;
import app.domain.models.AccountType;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class AccountRequest {
    @NotNull(message = "El tipo de cuenta no puede ser nulo")
    private AccountType accountType;

    @NotNull(message = "La moneda no puede ser nula")
    private String currency;

    @NotNull(message = "El ID del cliente no puede ser nulo")
    private Long clientId;

    private String accountNumber;
    private Double currentBalance;
}
