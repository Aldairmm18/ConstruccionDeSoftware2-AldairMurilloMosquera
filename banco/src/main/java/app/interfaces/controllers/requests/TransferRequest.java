package app.interfaces.controllers.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class TransferRequest {
    @NotBlank(message = "La cuenta origen no puede estar en blanco")
    private String sourceAccount;

    @NotBlank(message = "La cuenta destino no puede estar en blanco")
    private String targetAccount;

    @Positive(message = "El monto debe ser numéricamente positivo")
    private Double amount;
}
