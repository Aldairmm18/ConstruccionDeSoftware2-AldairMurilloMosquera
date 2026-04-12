package app.infrastructure.security;

import app.domain.ports.PasswordHasher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Infrastructure adapter: implements the domain PasswordHasher port using BCrypt.
 * Keeps Spring Security dependency out of the domain layer.
 */
@Component
public class BCryptPasswordHasherAdapter implements PasswordHasher {

    private final PasswordEncoder passwordEncoder;

    public BCryptPasswordHasherAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
