package app.domain.models;

import app.domain.Exceptions.InvalidNationalIdException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PersonClient extends Client {

    private String lastName;

    private LocalDate birthDate;

    // VALIDACION: Cédula colombiana
    @Override
    public void setDocument(String document) {
        if (document == null || document.isBlank()) {
            throw new InvalidNationalIdException("Cédula es obligatoria");
        }
        if (!document.matches("\\d{7,10}")) {
            throw new InvalidNationalIdException("Cédula debe tener entre 7 y 10 dígitos");
        }
        super.setDocument(document);
    }
}
