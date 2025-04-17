package com.fds.flex.core.portal.repository;

import com.fds.flex.core.portal.model.Header;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface HeaderRepository extends R2dbcRepository<Header, Long> {
    Mono<Header> findBySiteId(Long siteId);
} 