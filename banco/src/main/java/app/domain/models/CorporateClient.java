package app.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import app.domain.Exceptions.InvalidNitException;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CorporateClient extends Person {

    private String businessName;
    private String nit;
    private String legalRepresentative;
    private String username;

    public void setNit(String nit) {
        if (nit == null || nit.isBlank()) {
            throw new InvalidNitException("NIT is required");
        }
        if (!nit.matches("\\d{9}-\\d")) {
            throw new InvalidNitException("NIT must have the format XXXXXXXXX-X");
        }
        
        // Validate verification digit (Colombian NIT algorithm)
        String number = nit.substring(0, 9);
        char expectedDv = calculateNitDv(number);
        char inputDv = nit.charAt(10);
        
        if (expectedDv != inputDv) {
            throw new InvalidNitException("Incorrect NIT verification digit");
        }
        
        this.nit = nit;
    }

    private char calculateNitDv(String nitStr) {
        int[] weights = {71, 67, 59, 53, 47, 43, 41, 37, 29};
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(nitStr.charAt(i)) * weights[i];
        }
        int remainder = sum % 11;
        int dv = (remainder > 1) ? (11 - remainder) : remainder;
        return (dv == 10) ? '0' : Character.forDigit(dv, 10);
    }
}
