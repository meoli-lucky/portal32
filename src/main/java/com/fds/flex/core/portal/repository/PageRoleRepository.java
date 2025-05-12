package com.fds.flex.core.portal.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.fds.flex.core.portal.model.PageRole;

import reactor.core.publisher.Flux;

@Repository
public interface PageRoleRepository extends R2dbcRepository<PageRole, Long> {
    Flux<PageRole> findByPageId(Long pageId);
}
