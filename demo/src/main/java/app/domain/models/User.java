package app.domain.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@NoArgsConstructor
public class User extends Person {

    private String username;
    private SystemRole systemRole;
    private UserStatus userStatus;
}
