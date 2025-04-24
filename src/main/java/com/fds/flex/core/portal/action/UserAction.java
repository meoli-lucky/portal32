package com.fds.flex.core.portal.action;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import com.fds.flex.common.ultility.Validator;
import com.fds.flex.core.portal.model.User;
import com.fds.flex.core.portal.service.UserService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserAction {

    private final ReactiveAuthenticationManager authenticationManager;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public Mono<User> getUser(Long id) {
        return userService.findById(id);
    }

    public Mono<User> createUser(User user) {

        return userService.save(user);
    }

    public Mono<Void> deleteUser(Long id) {
        return userService.delete(id);
    }

    public Mono<User> updateUser(Long id, User user) {
        user.setId(id);
        return userService.save(user);
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
                
                return userService.save(user);
            })
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
    }

    private Mono<User> validateUser(User user, String action) {
        if (action.equals("create")) {
            if (Validator.isNull(user.getUsername()) || Validator.isNull(user.getPassword()) || Validator.isNull(user.getEmail())) {
                return Mono.error(new IllegalArgumentException("Username, password and email are required"));
            }else if (user.getUsername().length() < 6 || user.getUsername().length() > 20) {
                return Mono.error(new IllegalArgumentException("Full name is required"));
            }
        }else if (action.equals("resetPassword")) {
            if (Validator.isNull(user.getPassword())) {
                return Mono.error(new IllegalArgumentException("Password is required"));
            }
        }
        return Mono.just(user);
    }

    public static final String USERNAME_POLICY = "^(?!.*(admin|administrator))[a-zA-Z0-9]{3,12}$";
    public static final String PASSWORD_POLICY = "^(?=.*[0-9])(?=.*[A-Z]).{6,32}$";
    public static final String EMAIL_POLICY = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
}
