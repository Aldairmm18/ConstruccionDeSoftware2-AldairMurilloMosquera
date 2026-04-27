package app.interfaces.controllers;

import app.domain.models.User;
import app.domain.models.SystemRole;
import app.domain.models.UserStatus;
import app.domain.ports.UserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserPort userPort;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> payload) {
        try {
            User user = new User();
            user.setName((String) payload.get("name"));
            user.setDocument((String) payload.get("document"));
            user.setEmail((String) payload.get("email"));
            user.setPhone((String) payload.get("phone"));
            user.setAddress((String) payload.get("address"));
            user.setUsername((String) payload.get("username"));
            user.setPassword(passwordEncoder.encode((String) payload.get("password")));
            user.setSystemRole(SystemRole.valueOf((String) payload.get("role")));
            user.setUserStatus(UserStatus.ACTIVE);
            user.setBirthDate(LocalDate.of(2000, 1, 1));

            if (userPort.findByUsername(user.getUsername()).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
            }

            User saved = userPort.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "User created successfully",
                "username", saved.getUsername(),
                "role", saved.getSystemRole().name()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
