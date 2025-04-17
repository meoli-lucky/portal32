package com.fds.flex.core.portal.repository;

import com.fds.flex.core.portal.model.Footer;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface FooterRepository extends R2dbcRepository<Footer, Long> {
    Mono<Footer> findBySiteId(Long siteId);
} 