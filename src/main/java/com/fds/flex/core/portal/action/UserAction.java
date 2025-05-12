package com.fds.flex.core.portal.action;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import com.fds.flex.common.ultility.Validator;
import com.fds.flex.core.portal.model.User;
import com.fds.flex.core.portal.service.UserService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserAction {

    private final ReactiveAuthenticationManager authenticationManager;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public Mono<User> createUser(User user) {
        return validateUser(user, "create")
                .flatMap(this::prepareNewUser)
                .flatMap(userService::save);
    }

    public Mono<User> getUser(Long id) {
        return userService.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
    }

    public Flux<User> getAllUsers() {
        return userService.findAll();
    }

    public Mono<User> updateUser(Long id, User user) {
        return validateUser(user, "update")
                .flatMap(validatedUser -> prepareUpdatedUser(id, validatedUser))
                .flatMap(userService::save);
    }

    public Mono<Void> deleteUser(Long id) {
        userService.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
        return userService.delete(id);
    }

    public Mono<ResponseEntity<Map<String, String>>> login(String username, String password,
            ServerWebExchange exchange) {
        var authRequest = new UsernamePasswordAuthenticationToken(username, password);

        return authenticationManager.authenticate(authRequest)
                .doOnNext(auth -> SecurityContextHolder.getContext().setAuthentication(auth))
                .flatMap(auth -> exchange.getSession()
                        .doOnNext(session -> session.getAttributes().put("SPRING_SECURITY_CONTEXT",
                                SecurityContextHolder.getContext()))
                        .thenReturn(ResponseEntity.ok(Map.of("message", "Login success"))))
                .onErrorResume(e -> Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Invalid credentials")));
    }

    public Mono<ResponseEntity<Map<String, String>>> logout(ServerWebExchange exchange) {
        return exchange.getSession()
                .doOnNext(session -> session.invalidate())
                .thenReturn(ResponseEntity.ok(Map.of("message", "Logout success")));
    }

    public Mono<User> resetPassword(String username, String newPassword) {
        LocalDateTime now = LocalDateTime.now();
        // Validate the new password against the policy
        if (!newPassword.matches(PASSWORD_POLICY)) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Password does not meet the policy requirements"));
        }

        // Find the user by username
        return userService.findByUsername(username)
                .flatMap(user -> {
                    // Encrypt the new password using bcrypt
                    // String encryptedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

                    String encryptedPassword = passwordEncoder.encode(newPassword);

                    // Update the user's password
                    user.setPassword(encryptedPassword);
                    user.setModifiedDate(now);
                    return userService.save(user);
                })
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
    }

    private Mono<User> validateUser(User user, String action) {
        if (!Set.of("create", "update").contains(action)) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid action"));
        }

        return validateRequiredFields(user, action)
                .then(validateUsername(user, action))
                .then(validateEmail(user))
                .then(validatePassword(user))
                .then(checkUsernameExistence(user, action))
                .then(checkEmailExistence(user, action))
                .thenReturn(user);
    }

    private Mono<Void> validateRequiredFields(User user, String action) {
        if (action.equals("create") &&
                (Validator.isNull(user.getUsername()) || Validator.isNull(user.getPassword())
                        || Validator.isNull(user.getEmail()))) {
            return Mono.error(
                    new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username, password, and email are required"));
        }
        return Mono.empty();
    }

    private Mono<Void> validateUsername(User user, String action) {
        if (action.equals("create") &&
                (!user.getUsername().matches(USERNAME_POLICY) || user.getUsername().toLowerCase().contains("admin"))) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Username must be between 3 and 12 characters and not contain admin or administrator"));
        }
        return Mono.empty();
    }

    private Mono<Void> validateEmail(User user) {
        if (!user.getEmail().matches(EMAIL_POLICY)) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format"));
        }
        return Mono.empty();
    }

    private Mono<Void> validatePassword(User user) {
        if (!user.getPassword().matches(PASSWORD_POLICY)) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Password must be between 6 and 32 characters and contain at least one uppercase letter and one number"));
        }
        return Mono.empty();
    }

    private Mono<Void> checkUsernameExistence(User user, String action) {
        if (action.equals("create")) {
            return userService.findByUsername(user.getUsername())
                    .flatMap(existingUser -> Mono
                            .error(new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists")))
                    .then();
        }
        return Mono.empty();
    }

    private Mono<Void> checkEmailExistence(User user, String action) {
        return userService.findByEmail(user.getEmail()).flatMap(existingUser -> {
            if (action.equals("create")) {
                return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists"));
            } else {
                if (existingUser.getId() != user.getId()) {
                    return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists"));
                }
                return Mono.empty();
            }
        });
    }

    private Mono<User> prepareNewUser(User userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());
        user.setFullName(userDTO.getFullName());
        user.setAvatar(userDTO.getAvatar());
        user.setStatus(true);
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedDate(now);
        user.setModifiedDate(now);
        return Mono.just(user);
    }

    private Mono<User> prepareUpdatedUser(Long id, User userDTO) {
        return userService.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")))
                .flatMap(user -> {
                    user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
                    user.setEmail(userDTO.getEmail());
                    user.setFullName(userDTO.getFullName());
                    user.setAvatar(userDTO.getAvatar());
                    user.setModifiedDate(LocalDateTime.now());
                    return Mono.just(user);
                });
    }

    public static final String USERNAME_POLICY = "^(?!.*(admin|administrator))[a-zA-Z0-9]{3,12}$";
    public static final String PASSWORD_POLICY = "^(?=.*[0-9])(?=.*[A-Z]).{6,32}$";
    public static final String EMAIL_POLICY = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
}
