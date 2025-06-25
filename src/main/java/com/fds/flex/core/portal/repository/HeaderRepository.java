package com.fds.flex.core.portal.repository;

import com.fds.flex.core.portal.model.Header;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import org.springframework.stereotype.Repository;

@Repository
public interface HeaderRepository extends R2dbcRepository<Header, Long> {
    
    @Query("SELECT * FROM flex_header WHERE site_id = :siteId")
    Flux<Header> findBySiteId(Long siteId);
} 