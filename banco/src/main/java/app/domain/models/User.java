package app.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends Person {

  private String username;
  private SystemRole systemRole;
  private UserStatus userStatus;
}
