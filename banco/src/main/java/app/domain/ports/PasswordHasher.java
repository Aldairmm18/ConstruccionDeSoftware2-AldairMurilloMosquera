package app.domain.ports;

/**
 * Port for password hashing operations.
 * Domain depends on this abstraction; infrastructure provides the BCrypt implementation.
 */
public interface PasswordHasher {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
