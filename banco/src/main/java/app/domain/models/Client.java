package app.domain.models;

import app.domain.Exceptions.InvalidEmailException;
import app.domain.Exceptions.InvalidPhoneException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Client {
    
    @Id
    private String id;
    
    @Column(unique = true, nullable = false)
    private String identification;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String address;
    
    @Column(nullable = false)
    private String phone;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    // VALIDACIÓN: Email con formato correcto
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
    
    // VALIDACIÓN: Teléfono colombiano
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
