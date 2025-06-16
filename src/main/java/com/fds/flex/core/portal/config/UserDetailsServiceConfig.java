package com.fds.flex.core.portal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;

import com.fds.flex.core.portal.repository.UserRepository;
import com.fds.flex.core.portal.service.CustomReactiveUserDetailsService;
import com.fds.flex.core.portal.service.UserRoleService;

@Configuration
public class UserDetailsServiceConfig {

    private final UserRepository userRepository;

    private final UserRoleService userRoleService;

    public UserDetailsServiceConfig(UserRepository userRepository, UserRoleService userRoleService) {
        this.userRepository = userRepository;
        this.userRoleService = userRoleService;
    }

    @Bean
    public ReactiveUserDetailsService reactiveUserDetailsService() {
        return new CustomReactiveUserDetailsService(userRepository, userRoleService);
    }
}