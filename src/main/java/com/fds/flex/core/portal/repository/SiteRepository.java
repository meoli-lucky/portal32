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
    @Query("SELECT * FROM flex_site WHERE (:keyword IS NULL OR site_name LIKE :keyword) ORDER BY site_name")
    Flux<Site> filter(String keyword, Pageable pageable);

    @Query("SELECT COUNT(*) FROM flex_site WHERE (:keyword IS NULL OR site_name LIKE :keyword)")
    Mono<Long> countFilter(String keyword);
    
    @Query("SELECT * FROM flex_site WHERE :contextPath LIKE CONCAT(context, '%') AND context LIKE '/site/%'")
    Mono<Site> findByContextPathAndIgnoreRootSite(String contextPath);

    @Query("SELECT * FROM flex_site WHERE  context = '/'")
    Mono<Site> getRootSite();
} 