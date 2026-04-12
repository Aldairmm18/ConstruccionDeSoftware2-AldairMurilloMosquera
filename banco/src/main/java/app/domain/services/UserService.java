package app.domain.services;

import app.domain.Exceptions.*;
import app.domain.models.*;
import app.domain.ports.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for USER MANAGEMENT AND AUTHENTICATION
 * Handles: User registration, authentication, role management, and login security
 */
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AuditService auditService;
    
    private final Map<String, LoginAttempts> loginAttemptsTracker = new ConcurrentHashMap<>();
    
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long LOCKOUT_MINUTES = 15;
    
    // ==================== CREATE OPERATIONS ====================
    
    /**
     * Creates a new system user
     */
    @Transactional
    public User createUser(
            String username,
            String password,
            String fullName,
            SystemRole role) {
        
        validateUniqueUsername(username);
        validatePassword(password);
        
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(username);
        user.setFullName(fullName);
        user.setPassword(password); // Hashing happens in setter
        user.setSystemRole(role);
        user.setUserStatus(UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        
        User saved = userRepository.save(user);
        auditService.logOperation("USER_CREATED", saved.getId());
        
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
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> {
                attempts.recordFailedAttempt();
                auditService.logAuthenticationAttempt(username, false, "USER_NOT_FOUND");
                return new InvalidCredentialsException("Credenciales inválidas");
            });
        
        // Verify password
        if (!user.verifyPassword(password)) {
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
    @Transactional
    public void changePassword(String userId, String oldPassword, String newPassword) {
        User user = findByIdOrThrow(userId);
        
        // Verify old password
        if (!user.verifyPassword(oldPassword)) {
            throw new InvalidCredentialsException("Contraseña actual incorrecta");
        }
        
        validatePassword(newPassword);
        
        user.setPassword(newPassword);
        userRepository.save(user);
        
        auditService.logOperation("USER_PASSWORD_CHANGED", userId);
    }
    
    // ==================== ROLE MANAGEMENT ====================
    
    /**
     * Assigns a role to a user
     */
    @Transactional
    public User assignRole(String userId, SystemRole newRole) {
        User user = findByIdOrThrow(userId);
        
        SystemRole previousRole = user.getSystemRole();
        user.setSystemRole(newRole);
        
        User updated = userRepository.save(user);
        auditService.logRoleChange(userId, previousRole, newRole);
        
        return updated;
    }
    
    /**
     * Checks if user has required role
     */
    public boolean hasRole(String userId, SystemRole requiredRole) {
        User user = findByIdOrThrow(userId);
        return user.getSystemRole() == requiredRole;
    }
    
    /**
     * Checks if user has any of the required roles
     */
    public boolean hasAnyRole(String userId, SystemRole... requiredRoles) {
        User user = findByIdOrThrow(userId);
        return Arrays.asList(requiredRoles).contains(user.getSystemRole());
    }
    
    // ==================== QUERY OPERATIONS ====================
    
    public User findByIdOrThrow(String id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(
                "Usuario no encontrado: " + id));
    }
    
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    // ==================== VALIDATION METHODS ====================
    
    private void validateUniqueUsername(String username) {
        if (userRepository.existsByUsername(username)) {
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
        private LocalDateTime lastFailedAttempt;
        
        public void recordFailedAttempt() {
            this.failedAttempts++;
            this.lastFailedAttempt = LocalDateTime.now();
        }
        
        public boolean isLocked() {
            if (failedAttempts >= MAX_LOGIN_ATTEMPTS) {
                LocalDateTime lockoutExpiration = lastFailedAttempt.plusMinutes(LOCKOUT_MINUTES);
                return LocalDateTime.now().isBefore(lockoutExpiration);
            }
            return false;
        }
        
        public void reset() {
            this.failedAttempts = 0;
            this.lastFailedAttempt = null;
        }
    }
}
