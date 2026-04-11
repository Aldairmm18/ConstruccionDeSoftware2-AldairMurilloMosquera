package app.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
        validateNitFormat(nit);
        this.nit = nit;
    }


    private void validateNitFormat(String nitValue) {
        if (nitValue == null) return;
        
        if (nitValue.contains("-")) {
            String[] parts = nitValue.split("-");
            if (parts.length == 2) {
                try {
                    int dv = Integer.parseInt(parts[1]);
                    if (!calculateNitDv(parts[0], dv)) {
                        throw new IllegalArgumentException("Invalid NIT verification digit (DV)");
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid NIT format. Expected NNNNNNNN-D");
                }
            }
        }
    }


    private boolean calculateNitDv(String nitStr, int dv) {
        int[] weights = {3, 7, 13, 17, 19, 23, 29, 37, 41, 43, 47, 53, 59, 67, 71};
        int sum = 0;
        
        String reversed = new StringBuilder(nitStr).reverse().toString();
        
        for (int i = 0; i < reversed.length(); i++) {
            sum += Character.getNumericValue(reversed.charAt(i)) * weights[i];
        }
        
        int remainder = sum % 11;
        int calculated = remainder > 1 ? 11 - remainder : remainder;
        
        return calculated == dv;
    }

}
