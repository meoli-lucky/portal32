package com.fds.flex.core.portal.service;

import org.springframework.stereotype.Service;

import com.fds.flex.core.portal.model.User;
import com.fds.flex.core.portal.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Mono<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Mono<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Mono<User> save(User user) {
        return userRepository.save(user);
    }

    public Mono<Void> delete(Long id) {
        return userRepository.deleteById(id);
    }

    public Flux<User> findAll() {
        return userRepository.findAll();
    }
}
