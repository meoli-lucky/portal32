package com.fds.flex.core.portal.repository;

import com.fds.flex.core.portal.model.ViewTemplate;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ViewTemplateRepository extends R2dbcRepository<ViewTemplate, Long> {
    Mono<ViewTemplate> findByPageId(Long pageId);
} 