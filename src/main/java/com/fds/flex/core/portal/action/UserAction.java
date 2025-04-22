package com.fds.flex.core.portal.action;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import com.fds.flex.common.ultility.Validator;
import com.fds.flex.core.portal.model.User;
import com.fds.flex.core.portal.service.UserService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserAction {

    private final UserService userService;

    
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

    public Mono<User> login(String username, String password) {
        return userService.findByUsername(username)
            .flatMap(user -> {
                if (user.getPassword().equals(password)) {
                    // Tạo Authentication object
                    Authentication auth = new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
                    // Lưu vào SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    return Mono.just(user);
                } else {
                    return Mono.error(new IllegalArgumentException("Invalid username or password"));
                }
            });
    }

    public Mono<User> resetPassword(String username, String newPassword) {
        // Validate the new password against the policy
        if (!newPassword.matches(PASSWORD_POLICY)) {
            return Mono.error(new IllegalArgumentException("Password does not meet the policy requirements"));
        }

        // Find the user by username
        return userService.findByUsername(username)
            .flatMap(user -> {
                // Encrypt the new password using bcrypt
                String encryptedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

                // Update the user's password
                user.setPassword(encryptedPassword);
                return userService.save(user);
            })
            .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")));
    }

    private Mono<User> validateUser(User user, String action) {
        if (action.equals("create")) {
            if (Validator.isNull(user.getUsername()) || Validator.isNull(user.getPassword()) || Validator.isNull(user.getEmail())) {
                return Mono.error(new IllegalArgumentException("Username, password and email are required"));
            }else if (user.getUsername().length() < 6 || user.getUsername().length() > 20) {
                return Mono.error(new IllegalArgumentException("Full name is required"));
            }
        }
        return Mono.just(user);
    }

    public static final String USERNAME_POLICY = "^(?!.*(admin|administrator))[a-zA-Z0-9]{3,12}$";
    public static final String PASSWORD_POLICY = "^(?=.*[0-9])(?=.*[A-Z]).{6,32}$";
    public static final String EMAIL_POLICY = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
}
