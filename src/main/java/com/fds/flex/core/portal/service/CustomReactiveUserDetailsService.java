package com.fds.flex.core.portal.service;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import com.fds.flex.core.portal.repository.UserRepository;

import reactor.core.publisher.Mono;

public class CustomReactiveUserDetailsService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    private final UserRoleService userRoleService;

    public CustomReactiveUserDetailsService(UserRepository userRepository, UserRoleService userRoleService) {
        this.userRepository = userRepository;
        this.userRoleService = userRoleService;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
            .flatMap(user -> 
                userRoleService.findRoleNamesByUserId(user.getId())
                    .collectList() // Flux<String> -> Mono<List<String>>
                    .map(roleList -> User.withUsername(user.getUsername())
                            .password(user.getPassword())
                            .roles(roleList.toArray(new String[0])) // Convert List<String> -> String[]
                            .build()
                    )
            );
    }
}