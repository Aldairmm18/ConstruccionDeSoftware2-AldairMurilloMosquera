package app.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import app.domain.Exceptions.InvalidEmailException;
import app.domain.Exceptions.InvalidPhoneException;

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
            throw new InvalidEmailException("El email es obligatorio");
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!email.matches(emailRegex)) {
            throw new InvalidEmailException("Formato de email inválido: " + email);
        }
        this.email = email.toLowerCase().trim();
    }

    public void setPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new InvalidPhoneException("El teléfono es obligatorio");
        }
        if (!phone.matches("3\\d{9}")) {
            throw new InvalidPhoneException("El teléfono debe tener formato colombiano: 3XXXXXXXXX");
        }
        this.phone = phone;
    }
}
