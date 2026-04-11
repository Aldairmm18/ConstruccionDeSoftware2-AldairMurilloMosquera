package app.application.adapters.persistence.sql;

import app.application.adapters.persistence.sql.repositories.UserRepository;
import app.application.adapters.persistence.sql.entities.UserEntity;
import app.domain.models.User;
import app.domain.ports.UserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserPort {

  private final UserRepository userRepository;

  @Override
  public Optional<User> findByUsername(String username) {
    return userRepository.findByUsername(username)
        .map(entity -> entity.toDomain());
  }

  @Override
  public User save(User user) {
    UserEntity entity = UserEntity.fromDomain(user);
    UserEntity saved = userRepository.save(entity);
    return saved.toDomain();
  }
}
