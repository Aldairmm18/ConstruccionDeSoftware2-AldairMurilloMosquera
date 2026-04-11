package app.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import app.domain.Exceptions.EmailInvalidoException;
import app.domain.Exceptions.TelefonoInvalidoException;
import java.util.regex.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    private Long id;
    private String name;
    private String document;
    private String email;
    private String phone;
    private String address;

    public void setEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new EmailInvalidoException("Email es obligatorio");
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!email.matches(emailRegex)) {
            throw new EmailInvalidoException("Formato de email inválido: " + email);
        }
        this.email = email.toLowerCase().trim();
    }

    public void setPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new TelefonoInvalidoException("Teléfono es obligatorio");
        }
        if (!phone.matches("3\\d{9}")) {
            throw new TelefonoInvalidoException("Teléfono debe ser formato colombiano: 3XXXXXXXXX");
        }
        this.phone = phone;
    }
}
