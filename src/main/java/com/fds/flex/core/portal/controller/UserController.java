package com.fds.flex.core.portal.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.fds.flex.core.portal.action.UserAction;
import com.fds.flex.core.portal.model.User;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/nbio-ws/api/internal/users")
@RequiredArgsConstructor
public class UserController {

    private final UserAction userAction;

    @GetMapping
    public Flux<User> getAllUsers() {
        return userAction.getAllUsers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('system_manager')")
    public Mono<User> getUserById(@PathVariable Long id) {
        return userAction.getUser(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('admin') or hasRole('system_manager')")
    public Mono<User> createUser(@RequestBody User user) {
        return userAction.createUser(user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('system_manager')")
    public Mono<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return userAction.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('system_manager')")
    public Mono<Void> deleteUser(@PathVariable Long id) {
        return userAction.deleteUser(id);
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<Map<String, String>>> login(@RequestBody User user, ServerWebExchange exchange) {
        return userAction.login(user.getUsername(), user.getPassword(), exchange);  
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Map<String, String>>> logout(ServerWebExchange exchange) {
        return userAction.logout(exchange);
    }   

    @PostMapping("/reset-password")
    @PreAuthorize("hasRole('admin') or hasRole('system_manager')")
    public Mono<User> resetPassword(@RequestBody User user) {
        return userAction.resetPassword(user.getUsername(), user.getPassword());
    }   
}
