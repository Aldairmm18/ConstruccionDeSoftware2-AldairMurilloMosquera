package app.interfaces.controllers.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientRequest {
    @NotBlank(message = "El nombre no puede estar en blanco")
    private String name;

    @NotBlank(message = "El documento no puede estar en blanco")
    private String document;

    @NotBlank(message = "El email no puede estar en blanco")
    @Email(message = "Debe ser un email válido")
    private String email;

    private String phone;
    private String address;
    private java.time.LocalDate birthDate;
}
