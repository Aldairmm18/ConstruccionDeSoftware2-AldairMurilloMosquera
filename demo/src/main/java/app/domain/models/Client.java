package app.domain.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
public class Client {

    private long id;
    private String name;
    private String document;
    private String phone;
    private String email;
    private String address;
    private Date birthDate;
    private ClientStatus clientStatus;
}
