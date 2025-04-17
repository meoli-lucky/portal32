package com.fds.flex.core.portal.repository;

import com.fds.flex.core.portal.model.Page;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PageRepository extends R2dbcRepository<Page, Long> {
    Flux<Page> findBySiteId(Long siteId);
    Mono<Page> findByFriendlyURLAndSiteId(String friendlyURL, Long siteId);
} 