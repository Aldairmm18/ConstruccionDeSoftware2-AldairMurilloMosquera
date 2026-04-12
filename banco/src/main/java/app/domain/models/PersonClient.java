package app.domain.models;

import app.domain.Exceptions.InvalidNationalIdException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PersonClient extends Client {
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(nullable = false)
    private LocalDate birthDate;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id")
    private BankAccount account;
    
    // VALIDACIÓN: Cédula colombiana
    @Override
    public void setIdentification(String identification) {
        if (identification == null || identification.isBlank()) {
            throw new InvalidNationalIdException("Cédula es obligatoria");
        }
        if (!identification.matches("\\d{7,10}")) {
            throw new InvalidNationalIdException("Cédula debe tener entre 7 y 10 dígitos");
        }
        super.setIdentification(identification);
    }
}
