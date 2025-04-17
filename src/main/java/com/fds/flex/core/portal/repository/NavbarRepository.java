package com.fds.flex.core.portal.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.fds.flex.core.portal.model.Navbar;

import reactor.core.publisher.Flux;

@Repository
public interface NavbarRepository extends R2dbcRepository<Navbar, Long> {
    Flux<Navbar> findBySiteIdAndParentIdIsNull(Long siteId);
    Flux<Navbar> findByParentId(Long parentId);
} 