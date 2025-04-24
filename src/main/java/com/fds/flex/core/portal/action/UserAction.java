package com.fds.flex.core.portal.action;

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

    public Mono<User> getUser(Long id) {
        return userService.findById(id)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
    }

    public Mono<User> createUser(User user) {
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedDate(now);
        user.setModifiedDate(now);
        return validateUser(user, "create")
            .flatMap(userService::save);
    }

    public Mono<Void> deleteUser(Long id) {
       userService.findById(id)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
        return userService.delete(id);
    }

    public Mono<User> updateUser(Long id, User user) {
        LocalDateTime now = LocalDateTime.now();
        user.setId(id);
        user.setCreatedDate(now);
        user.setModifiedDate(now);
        return validateUser(user, "update")
            .flatMap(userService::save);
    }

    public Mono<ResponseEntity<Map<String, String>>> login(String username, String password, ServerWebExchange exchange) {
        var authRequest = new UsernamePasswordAuthenticationToken(username, password);

        return authenticationManager.authenticate(authRequest)
            .doOnNext(auth -> SecurityContextHolder.getContext().setAuthentication(auth))
            .flatMap(auth -> exchange.getSession()
                .doOnNext(session -> session.getAttributes().put("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext()))
                .thenReturn(ResponseEntity.ok(Map.of("message", "Login success")))
            )
            .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid credentials"))));
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
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,"Password does not meet the policy requirements"));
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
        if (action.equals("create")) {
            if (Validator.isNull(user.getUsername()) || Validator.isNull(user.getPassword()) || Validator.isNull(user.getEmail())) {
                return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username, password and email are required"));
            } else if (!user.getUsername().matches(USERNAME_POLICY)) {
                return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username must be between 3 and 12 characters and not contain admin or administrator"));
            } else if (!user.getEmail().matches(EMAIL_POLICY)) {
                return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format"));
            } else if (!user.getPassword().matches(PASSWORD_POLICY)) {
                return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be between 6 and 32 characters and contain at least one uppercase letter and one number"));
            } else {
                return userService.findByUsername(user.getUsername())
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
            }
        } else if (action.equals("update")) {
             if (!user.getEmail().matches(EMAIL_POLICY)) {
                return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format"));
            } else if (!user.getPassword().matches(PASSWORD_POLICY)) {
                return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be between 6 and 32 characters and contain at least one uppercase letter and one number"));
            } else {
                return userService.findByUsername(user.getUsername())
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
            }
        }
        return Mono.just(user);
    }

    public Flux<User> getAllUsers() {
        return userService.findAll();
    }

    public static final String USERNAME_POLICY = "^(?!.*(admin|administrator))[a-zA-Z0-9]{3,12}$";
    public static final String PASSWORD_POLICY = "^(?=.*[0-9])(?=.*[A-Z]).{6,32}$";
    public static final String EMAIL_POLICY = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
}
