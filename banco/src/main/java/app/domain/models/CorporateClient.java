package app.domain.models;

import app.domain.Exceptions.InvalidNitException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CorporateClient extends Client {

    private String businessName;

    private String nit;

    private String legalRepresentative;

    private String username;

    // VALIDACION: NIT con dígito verificador
    public void setNit(String nit) {
        if (nit == null || nit.isBlank()) {
            throw new InvalidNitException("NIT es obligatorio");
        }
        if (!nit.matches("\\d{9}-\\d")) {
            throw new InvalidNitException("NIT debe tener formato XXXXXXXXX-X");
        }

        // Validar dígito verificador
        String numero = nit.substring(0, 9);
        char dvEsperado = calcularDVNIT(numero);
        char dvIngresado = nit.charAt(10);

        if (dvEsperado != dvIngresado) {
            throw new InvalidNitException("Dígito verificador de NIT inválido");
        }

        this.nit = nit;
    }

    private char calcularDVNIT(String nit) {
        int[] pesos = {71, 67, 59, 53, 47, 43, 41, 37, 29};
        int suma = 0;
        for (int i = 0; i < 9; i++) {
            suma += Character.getNumericValue(nit.charAt(i)) * pesos[i];
        }
        int residuo = suma % 11;
        int dv = (residuo > 1) ? (11 - residuo) : residuo;
        return (dv == 10) ? '0' : Character.forDigit(dv, 10);
    }
}
