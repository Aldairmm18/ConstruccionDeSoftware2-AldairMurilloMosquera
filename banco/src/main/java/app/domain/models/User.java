package app.domain.models;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends Person {

    private LocalDate birthDate;
    private String username;
    private String password;
    private SystemRole systemRole;
    private UserStatus userStatus;

    public void setPassword(String password) {
        if (password == null || (!password.startsWith("$2a$") && !password.startsWith("$2b$") && !password.startsWith("$2y$"))) {
            throw new IllegalArgumentException("Password must be encrypted (BCrypt required)");
        }
        this.password = password;
    }
}
