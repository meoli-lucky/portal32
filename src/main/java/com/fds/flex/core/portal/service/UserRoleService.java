package com.fds.flex.core.portal.service;

import org.springframework.stereotype.Service;

import com.fds.flex.core.portal.model.Role;
import com.fds.flex.core.portal.model.UserRole;
import com.fds.flex.core.portal.repository.RoleRepository;
import com.fds.flex.core.portal.repository.UserRoleRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    public Mono<UserRole> findById(Long id) {
        return userRoleRepository.findById(id);
    }
    

    public Flux<UserRole> findByUserId(Long userId) {
        return userRoleRepository.findByUserId(userId);
    }

    public Mono<UserRole> save(UserRole userRole) {
        return userRoleRepository.save(userRole);
    }

    public Mono<Void> delete(Long id) {
        return userRoleRepository.deleteById(id);
    }
    
    public Flux<Role> findRolesByUserId(Long userId) {
        return userRoleRepository.findByUserId(userId).flatMap(userRole -> roleRepository.findById(userRole.getRoleId()));
    }

    public Flux<String> findRoleNamesByUserId(Long userId) {
        Flux<Role> roles = userRoleRepository.findByUserId(userId).flatMap(userRole -> roleRepository.findById(userRole.getRoleId()));
        
        Flux<String> roleNames = roles.map(Role::getRoleName);
        
        return roleNames;
    }
}
