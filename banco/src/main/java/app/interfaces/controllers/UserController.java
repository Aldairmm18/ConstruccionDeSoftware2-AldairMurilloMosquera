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
            String username = (String) payload.get("username");
            String password = (String) payload.get("password");
            String name = (String) payload.get("name");
            String document = (String) payload.get("document");
            String email = (String) payload.get("email");
            String phone = (String) payload.get("phone");
            String address = (String) payload.get("address");
            String role = (String) payload.get("role");

            // Check if username already exists
            if (userPort.findByUsername(username).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
            }

            User user = new User();
            user.setName(name);
            user.setDocument(document);
            user.setEmail(email);
            user.setPhone(phone);
            user.setAddress(address);
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setSystemRole(SystemRole.valueOf(role));
            user.setUserStatus(UserStatus.ACTIVE);
            user.setBirthDate(LocalDate.of(2000, 1, 1));

            User saved = userPort.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "id", saved.getId(),
                "username", saved.getUsername(),
                "role", saved.getSystemRole().name(),
                "status", saved.getUserStatus().name()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
