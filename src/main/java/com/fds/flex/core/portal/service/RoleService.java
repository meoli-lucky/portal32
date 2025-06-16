package com.fds.flex.core.portal.service;

import org.springframework.stereotype.Service;

import com.fds.flex.core.portal.model.Role;
import com.fds.flex.core.portal.repository.RoleRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Mono<Role> findById(Long id) {
        return roleRepository.findById(id);
    }

    public Mono<Role> save(Role role) {
        return roleRepository.save(role);
    }

    public Mono<Void> delete(Long id) {
        return roleRepository.deleteById(id);
    }
}
