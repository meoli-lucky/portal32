package com.fds.flex.core.portal.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.fds.flex.core.portal.model.UserRole;

import reactor.core.publisher.Flux;

@Repository
public interface UserRoleRepository extends R2dbcRepository<UserRole, Long> {

    Flux<UserRole> findByUserId(Long userId);
}
