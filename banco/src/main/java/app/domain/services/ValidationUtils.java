package app.domain.services;

import java.util.regex.Pattern;

public class ValidationUtils {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);


    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }


    public static boolean validateNitVerificationDigit(String nit, int dv) {
        if (nit == null || nit.isEmpty()) {
            return false;
        }
        
        try {
            return calculateNitVerificationDigit(nit) == dv;
        } catch (Exception e) {
            return false;
        }
    }


    private static int calculateNitVerificationDigit(String nit) {
        int[] weights = {3, 7, 13, 17, 19, 23, 29, 37, 41, 43, 47, 53, 59, 67, 71};
        int sum = 0;
        
        String reversedNit = new StringBuilder(nit).reverse().toString();
        
        for (int i = 0; i < reversedNit.length(); i++) {
            int digit = Character.getNumericValue(reversedNit.charAt(i));
            sum += digit * weights[i];
        }
        
        int remainder = sum % 11;
        
        return remainder > 1 ? 11 - remainder : remainder;
    }

}
