package app.domain.models;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends Person {

    private LocalDate birthDate;
    private String username;
    
    @JsonIgnore
    private String passwordHash;
    
    private SystemRole systemRole;
    private UserStatus userStatus;

    public void setPassword(String newPassword) {
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        this.passwordHash = encoder.encode(newPassword);
    }

    public boolean verifyPassword(String enteredPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(enteredPassword, this.passwordHash);
    }

    public String getPassword() {
        return passwordHash;
    }

    @JsonIgnore
    public String getPasswordHash() {
        return passwordHash;
    }
}
