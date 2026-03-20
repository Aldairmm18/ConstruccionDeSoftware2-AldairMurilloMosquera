package app.infrastructure.persistence;

import app.domain.models.SystemRole;
import app.domain.models.User;
import app.domain.models.UserStatus;
import jakarta.persistence.Entity;
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

  private String username;

  @Enumerated(EnumType.STRING)
  private SystemRole systemRole;

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
    user.setSystemRole(getSystemRole());
    user.setUserStatus(getUserStatus());
    return user;
  }
}
