package com.fds.flex.core.portal.repository;

import com.fds.flex.core.portal.model.Site;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface SiteRepository extends R2dbcRepository<Site, Long> {
    @Query("SELECT * FROM site WHERE (:keyword IS NULL OR site_name LIKE :keyword) ORDER BY site_name")
    Flux<Site> filter(String keyword, Pageable pageable);

    @Query("SELECT COUNT(*) FROM site WHERE (:keyword IS NULL OR site_name LIKE :keyword)")
    Mono<Long> countFilter(String keyword);
    
    @Query("SELECT * FROM site WHERE context_path = :contextPath")
    Mono<Site> findByContextPath(String contextPath);
} 