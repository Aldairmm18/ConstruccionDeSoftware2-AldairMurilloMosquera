package app.domain.services;

import app.domain.models.User;
import app.domain.ports.UserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserPort userPort;
    
    private final PasswordEncoder passwordEncoder;


    public User save(User user) {
        if (user.getPassword() != null && !isEncoded(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        return userPort.save(user);
    }


    public Optional<User> findByUsername(String username) {
        return userPort.findByUsername(username);
    }


    private boolean isEncoded(String password) {
        return password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$");
    }

}
