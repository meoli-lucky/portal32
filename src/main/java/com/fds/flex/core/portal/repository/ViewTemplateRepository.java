package com.fds.flex.core.portal.repository;

import com.fds.flex.core.portal.model.ViewTemplate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ViewTemplateRepository extends R2dbcRepository<ViewTemplate, Long> {

    Flux<ViewTemplate> findBySiteId(Long siteId);
    
    @Query("SELECT * FROM view_template WHERE site_id = :siteId AND (:keyword IS NULL OR (template_name LIKE :keyword OR template_name LIKE :keyword)) ORDER BY id LIMIT :#{#pageable.pageSize} OFFSET :#{#pageable.offset}")
    Flux<ViewTemplate> filter(Long siteId, String keyword, Pageable pageable);

    @Query("SELECT COUNT(*) FROM view_template WHERE site_id = :siteId AND (:keyword IS NULL OR (template_name LIKE :keyword OR template_name LIKE :keyword))")
    Mono<Long> countFilter(Long siteId, String keyword);
} 