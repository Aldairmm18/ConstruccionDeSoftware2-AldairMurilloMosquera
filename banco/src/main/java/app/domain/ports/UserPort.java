package app.domain.ports;

import app.domain.models.User;
import java.util.Optional;

public interface UserPort {

  Optional<User> findByUsername(String username);
  
  User findById(Long id);

  User save(User user);
}
