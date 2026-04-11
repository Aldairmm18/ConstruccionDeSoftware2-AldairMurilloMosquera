package app.domain.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * CORRECCIÓN 1: Clase base abstracta para todos los clientes del banco.
 * Hereda de Person para centralizar los datos básicos y añade campos específicos de negocio.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Client extends Person {

    private String clientId; // ID de negocio (ej: CLI-001)
    private LocalDate registrationDate;
    private ClientRole clientRole;
    private List<BankAccount> accounts = new ArrayList<>();

}
