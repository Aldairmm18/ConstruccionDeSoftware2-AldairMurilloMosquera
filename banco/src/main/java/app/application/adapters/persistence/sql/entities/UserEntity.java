package app.application.adapters.persistence.sql.entities;

import app.domain.models.SystemRole;
import app.domain.models.User;
import app.domain.models.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.time.LocalDate;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends PersonEntity {

  @Column(name = "birth_date", nullable = false)
  private LocalDate birthDate;

  @Column(name = "username", unique = true, nullable = false)
  private String username;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "system_role", nullable = false)
  @Enumerated(EnumType.STRING)
  private SystemRole systemRole;

  @Column(name = "user_status")
  @Enumerated(EnumType.STRING)
  private UserStatus userStatus;

  public static UserEntity fromDomain(User user) {
    if (user == null) {
      return null;
    }
    UserEntity entity = new UserEntity();
    entity.setId(user.getId());
    entity.setName(user.getName());
    entity.setDocument(user.getDocument());
    entity.setEmail(user.getEmail());
    entity.setPhone(user.getPhone());
    entity.setAddress(user.getAddress());
    entity.setUsername(user.getUsername());
    entity.setPassword(user.getPassword());
    entity.setSystemRole(user.getSystemRole());
    entity.setUserStatus(user.getUserStatus());
    return entity;
  }

  public User toDomain() {
    User user = new User();
    user.setId(getId());
    user.setName(getName());
    user.setDocument(getDocument());
    user.setEmail(getEmail());
    user.setPhone(getPhone());
    user.setAddress(getAddress());
    user.setUsername(getUsername());
    user.setPassword(getPassword());
    user.setSystemRole(getSystemRole());
    user.setUserStatus(getUserStatus());
    return user;
  }
}
