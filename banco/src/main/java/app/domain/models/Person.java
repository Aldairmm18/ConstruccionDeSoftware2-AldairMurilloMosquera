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
            throw new InvalidEmailException("Email is required");
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!email.matches(emailRegex)) {
            throw new InvalidEmailException("Invalid email format: " + email);
        }
        this.email = email.toLowerCase().trim();
    }

    public void setPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new InvalidPhoneException("Phone is required");
        }
        if (!phone.matches("3\\d{9}")) {
            throw new InvalidPhoneException("Phone must be in Colombian format: 3XXXXXXXXX");
        }
        this.phone = phone;
    }
}
