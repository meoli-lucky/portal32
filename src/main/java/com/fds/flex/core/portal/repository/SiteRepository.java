package com.fds.flex.core.portal.repository;

import com.fds.flex.core.portal.model.Site;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface SiteRepository extends R2dbcRepository<Site, Long> {
    Mono<Site> findByFriendlyURL(String friendlyURL);
} 