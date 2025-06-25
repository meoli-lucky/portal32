package com.fds.flex.core.portal.repository;

import com.fds.flex.core.portal.model.Footer;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import org.springframework.stereotype.Repository;

@Repository
public interface FooterRepository extends R2dbcRepository<Footer, Long> {
    
    @Query("SELECT * FROM flex_footer WHERE site_id = :siteId")
    Flux<Footer> findBySiteId(Long siteId);
} 