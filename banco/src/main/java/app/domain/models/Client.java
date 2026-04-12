package app.domain.models;

import app.domain.Exceptions.InvalidEmailException;
import app.domain.Exceptions.InvalidPhoneException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    private Long id;

    private String document;

    private String name;

    private String address;

    private String phone;

    private String email;

    private ClientStatus clientStatus;

    // VALIDACION: Email con formato correcto
    public void setEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new InvalidEmailException("Email es obligatorio");
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!email.matches(emailRegex)) {
            throw new InvalidEmailException("Formato de email inválido: " + email);
        }
        this.email = email.toLowerCase().trim();
    }

    // VALIDACION: Teléfono colombiano
    public void setPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new InvalidPhoneException("Teléfono es obligatorio");
        }
        if (!phone.matches("3\\d{9}")) {
            throw new InvalidPhoneException("Teléfono debe ser formato colombiano: 3XXXXXXXXX");
        }
        this.phone = phone;
    }
}
