package app.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;

    private String name;

    private String document;

    private String email;

    private String phone;

    private String address;

    private LocalDate birthDate;

    private String username;

    private String password;

    private SystemRole systemRole;

    private UserStatus userStatus;

    // Plain validation only - no hashing here (hashing done in UserService)
    public void setPassword(String newPassword) {
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }
        this.password = newPassword;
    }

    // Plain equals comparison
    public boolean verifyPassword(String enteredPassword) {
        return this.password != null && this.password.equals(enteredPassword);
    }
}
