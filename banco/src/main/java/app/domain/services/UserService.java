package app.domain.services;

import app.domain.Exceptions.*;
import app.domain.models.*;
import app.domain.ports.PasswordHasher;
import app.domain.ports.UserPort;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for USER MANAGEMENT AND AUTHENTICATION
 * Handles: User registration, authentication, role management, and login security
 */
public class UserService {

    private final UserPort userPort;
    private final AuditService auditService;
    private final PasswordHasher passwordHasher;
    private final Map<String, LoginAttempts> loginAttemptsTracker = new ConcurrentHashMap<>();

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long LOCKOUT_MINUTES = 15;

    public UserService(UserPort userPort, AuditService auditService, PasswordHasher passwordHasher) {
        this.userPort = userPort;
        this.auditService = auditService;
        this.passwordHasher = passwordHasher;
    }

    // ==================== CREATE OPERATIONS ====================

    /**
     * Creates a new system user
     */
    public User createUser(
            String username,
            String password,
            String name,
            SystemRole role) {

        validateUniqueUsername(username);
        validatePassword(password);

        User user = new User();
        user.setId(null);
        user.setUsername(username);
        user.setName(name);
        user.setPassword(passwordHasher.encode(password));
        user.setSystemRole(role);
        user.setUserStatus(UserStatus.ACTIVE);

        User saved = userPort.save(user);
        auditService.logOperation("USER_CREATED", saved.getId() != null ? saved.getId().toString() : null);

        return saved;
    }

    // ==================== AUTHENTICATION OPERATIONS ====================

    /**
     * Authenticates a user with username and password
     * Implements rate limiting to prevent brute force attacks
     */
    public User authenticate(String username, String password) {
        LoginAttempts attempts = loginAttemptsTracker.computeIfAbsent(
            username, k -> new LoginAttempts());

        // Check if account is locked due to too many failed attempts
        if (attempts.isLocked()) {
            auditService.logAuthenticationAttempt(username, false, "ACCOUNT_LOCKED");
            throw new AccountBlockedException(
                String.format("Cuenta bloqueada por %d minutos debido a demasiados intentos fallidos",
                    LOCKOUT_MINUTES));
        }

        // Find user
        Optional<User> userOpt = userPort.findByUsername(username);
        if (userOpt.isEmpty()) {
            attempts.recordFailedAttempt();
            auditService.logAuthenticationAttempt(username, false, "USER_NOT_FOUND");
            throw new InvalidCredentialsException("Credenciales inválidas");
        }
        User user = userOpt.get();

        // Verify password using PasswordHasher port
        if (!passwordHasher.matches(password, user.getPassword())) {
            attempts.recordFailedAttempt();
            auditService.logAuthenticationAttempt(username, false, "INVALID_PASSWORD");
            throw new InvalidCredentialsException("Credenciales inválidas");
        }

        // Success - reset attempts
        attempts.reset();
        auditService.logAuthenticationAttempt(username, true, null);

        return user;
    }

    /**
     * Changes user password
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = findByIdOrThrow(userId);

        // Verify old password
        if (!passwordHasher.matches(oldPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Contraseña actual incorrecta");
        }

        validatePassword(newPassword);

        user.setPassword(passwordHasher.encode(newPassword));
        userPort.save(user);

        auditService.logOperation("USER_PASSWORD_CHANGED", userId.toString());
    }

    // ==================== ROLE MANAGEMENT ====================

    /**
     * Assigns a role to a user
     */
    public User assignRole(Long userId, SystemRole newRole) {
        User user = findByIdOrThrow(userId);

        SystemRole previousRole = user.getSystemRole();
        user.setSystemRole(newRole);

        User updated = userPort.save(user);
        auditService.logRoleChange(userId.toString(), previousRole, newRole);

        return updated;
    }

    /**
     * Checks if user has required role
     */
    public boolean hasRole(Long userId, SystemRole requiredRole) {
        User user = findByIdOrThrow(userId);
        return user.getSystemRole() == requiredRole;
    }

    /**
     * Checks if user has any of the required roles
     */
    public boolean hasAnyRole(Long userId, SystemRole... requiredRoles) {
        User user = findByIdOrThrow(userId);
        return Arrays.asList(requiredRoles).contains(user.getSystemRole());
    }

    // ==================== QUERY OPERATIONS ====================

    public User findByIdOrThrow(Long id) {
        User user = userPort.findById(id);
        if (user == null) {
            throw new UserNotFoundException("Usuario no encontrado: " + id);
        }
        return user;
    }

    public User findById(Long id) {
        return userPort.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userPort.findByUsername(username);
    }

    // ==================== VALIDATION METHODS ====================

    private void validateUniqueUsername(String username) {
        if (userPort.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException(
                "Ya existe un usuario con username: " + username);
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException(
                "La contraseña debe tener al menos 8 caracteres");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException(
                "La contraseña debe contener al menos una letra mayúscula");
        }

        if (!password.matches(".*[0-9].*")) {
            throw new IllegalArgumentException(
                "La contraseña debe contener al menos un número");
        }
    }

    // ==================== LOGIN ATTEMPTS TRACKING ====================

    /**
     * Inner class to track login attempts and implement account lockout
     */
    private static class LoginAttempts {
        private int failedAttempts = 0;
        private java.time.LocalDateTime lastFailedAttempt;

        public void recordFailedAttempt() {
            this.failedAttempts++;
            this.lastFailedAttempt = java.time.LocalDateTime.now();
        }

        public boolean isLocked() {
            if (failedAttempts >= MAX_LOGIN_ATTEMPTS) {
                java.time.LocalDateTime lockoutExpiration = lastFailedAttempt.plusMinutes(LOCKOUT_MINUTES);
                return java.time.LocalDateTime.now().isBefore(lockoutExpiration);
            }
            return false;
        }

        public void reset() {
            this.failedAttempts = 0;
            this.lastFailedAttempt = null;
        }
    }
}
