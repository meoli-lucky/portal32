package com.fds.flex.core.portal.service;

import org.springframework.stereotype.Service;

import com.fds.flex.core.portal.model.UserRole;
import com.fds.flex.core.portal.repository.UserRoleRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    public Mono<UserRole> findById(Long id) {
        return userRoleRepository.findById(id);
    }

    public Mono<UserRole> save(UserRole userRole) {
        return userRoleRepository.save(userRole);
    }

    public Mono<Void> delete(Long id) {
        return userRoleRepository.deleteById(id);
    }
    
}
